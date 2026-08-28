package de.visualdigits.newshomereader.data.util

import co.touchlab.kermit.Logger
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogCategory
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogItem
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths
import kotlin.time.Duration.Companion.milliseconds

object CatalogScraper {

    private val log = Logger.withTag("CatalogScraper")

    private val jsonMapper = Json {
        prettyPrint = true
    }

    /**
     * Scrape a catalog from https://www.rss-verzeichnis.de
     *
     * @param httpClient The OkHttpClient to use for scraping.
     */
    suspend fun scrapeToFile(
        httpClient: HttpClient,
    ) {
        val baseUrl = "https://www.rss-verzeichnis.de"
        log.i("Scraping catalog from baseUrl: $$baseUrl")
        val newsCategories = scrapeMainCategories(httpClient, baseUrl)
        val newsCatalog = NewsFeedCatalog(
            baseUrl = baseUrl,
            date = KmpOffsetDateTime.now(),
            categories = newsCategories
        )

        val json = jsonMapper.encodeToString(newsCatalog)

        val directory = Paths.get(File(".").canonicalPath, "src/commonMain/composeResources/files").toFile()
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                log.e("Could not create catalog directory")
            }
        }
        val targetFile =
            File(directory, "catalog_rss-verzeichnis.json")
        targetFile.writeText(json)
    }

    /**
     * Scrapes the top level categories from the main page.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     * @param baseUrl The url of the catogry page.
     */
    private suspend fun scrapeMainCategories(
        httpClient: HttpClient,
        baseUrl: String
    ): List<NewsFeedCatalogCategory> {
        log.i("Scraping toplevel categories")
        return readUrl(httpClient, baseUrl)
            ?.let { html ->
                Ksoup.parseBodyFragment(html).select("ul[class=verzeichnis level1] > li")
                    .map { elem ->
                        delay(2000.milliseconds) // anti anti scrape
                        val a = elem.selectFirst("> a")
                        val mainCategoryName = a?.attr("title") ?: ""
                        val categoryPageUrl = makeUrlAbsolute(baseUrl, a?.attr("href") ?: "")
                        NewsFeedCatalogCategory(
                            name = mainCategoryName,
                            url = categoryPageUrl,
                            subCategories = scrapeSubCategories(
                                httpClient = httpClient,
                                rootUrl = baseUrl,
                                baseUrl = categoryPageUrl,
                                mainCategoryName = mainCategoryName
                            )
                        )
                    }
            } ?: listOf()
    }

    /**
     * Scrapes the sub categories from a main category page.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     * @param rootUrl The root url of the main page to filter out feed entries which point to the main page.
     * @param baseUrl The url of the sub category page.
     * @param mainCategoryName The name of the top level category.
     */
    private suspend fun scrapeSubCategories(
        httpClient: HttpClient,
        rootUrl: String,
        baseUrl: String,
        mainCategoryName: String
    ): List<NewsFeedCatalogCategory> {
        log.i("  Scraping sub categories for category $baseUrl")
        return readUrl(httpClient, baseUrl)
            ?.let { html ->
                Ksoup.parseBodyFragment(html)
                    .select("ul[class=verzeichnis level2] > li")
                    .map { li ->
                        val a = li.select("a")
                        val subCategoryPageUrl = makeUrlAbsolute(baseUrl, a.attr("href"))
                        val subCategoryName = a.attr("title")
                        NewsFeedCatalogCategory(
                            name = subCategoryName,
                            url = subCategoryPageUrl,
                            feeds = scrapeFeeds(
                                httpClient = httpClient,
                                rootUrl = rootUrl,
                                baseUrl = subCategoryPageUrl,
                                subCategoryName = "$mainCategoryName/$subCategoryName"
                            ),
                        )
                    }
            } ?: listOf()
    }

    /**
     * Wrapper which determines the feed pages to scrape from
     * a sub category page also takes paginated pages into account.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     * @param rootUrl The root url of the main page to filter out feed entries which point to the main page.
     * @param baseUrl The url of the category page.
     * @param subCategoryName The qualified name of the sub category.
     */
    private suspend fun scrapeFeeds(
        httpClient: HttpClient,
        rootUrl: String,
        baseUrl: String,
        subCategoryName: String
    ): List<NewsFeedCatalogItem> {
        log.i("    Scraping feeds from category '$subCategoryName'")

        return readUrl(httpClient, baseUrl)
            ?.let { html ->
                val doc = Ksoup.parseBodyFragment(html)
                val pageDocs = doc
                    .select("div.pagination")
                    .select("a")
                    .mapNotNull { a ->
                        val pageUrl = makeUrlAbsolute(baseUrl, a.attr("href"))
                        readUrl(httpClient, pageUrl)?.let {html ->
                            Ksoup.parse(html)
                        }
                    }

                val firstFeeds = scrapeFeedPage(
                    httpClient = httpClient,
                    rootUrl = rootUrl,
                    baseUrl = baseUrl,
                    doc = doc,
                    subCategoryName = subCategoryName,
                    page = 1
                )
                val pageFeeds = pageDocs.flatMapIndexed { index, pageDoc ->
                    scrapeFeedPage(
                        httpClient = httpClient,
                        rootUrl = rootUrl,
                        baseUrl = baseUrl,
                        doc = pageDoc,
                        subCategoryName = subCategoryName,
                        page = index + 2
                    )
                }

                firstFeeds + pageFeeds
            } ?: listOf()
    }

    /**
     * Scrapes feed description pages as determined by scrapeFeeds()
     *
     * @param httpClient The OkHttpClient to use for scraping.
     * @param rootUrl The root url of the main page to filter out feed entries which point to the main page.
     * @param baseUrl The url of the feed description page.
     * @param doc The ksoup document from the sub category page to avoid double parsing.
     * @param subCategoryName The qualified name of the sub category.
     * @param page The page number from the pagination
     */
    private suspend fun scrapeFeedPage(
        httpClient: HttpClient,
        rootUrl: String,
        baseUrl: String,
        doc: Document,
        subCategoryName: String,
        page: Int
    ): List<NewsFeedCatalogItem> {
        log.i("      Scraping feed page $page for category '$subCategoryName'")
        return (doc
            .selectFirst("div:containsOwn(Feeds)")?.parent()?.parent()?.parent()
            ?.nextElementSibling()
            ?.select("table")
            ?.select("td")
            ?.map { elem ->
                val a = elem.select("a")
                val descriptionUrl = a.attr("href")
                val name = a.text()
                val description = elem.select("div").text()
                Triple(name, description, makeUrlAbsolute(baseUrl, descriptionUrl))
            }
            ?.filter { t -> t.third.isNotEmpty() && t.third != rootUrl }
            ?.mapNotNull { t ->
                val feed = scrapeFeedUrl(httpClient, t.third)
                if (feed != null) {
                    NewsFeedCatalogItem(
                        name = t.first,
                        descriptionShort = t.second,
                        descriptionLong = feed.first,
                        url = feed.second
                    )
                } else {
                    null
                }
            }
            ?: listOf())
    }

    /**
     * Scrapes the feed url itself from a feed description page.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     * @param baseUrl The url of the feed page.
     */
    private suspend fun scrapeFeedUrl(
        httpClient: HttpClient,
        baseUrl: String
    ): Pair<String, String>? {
        log.i("        Scraping feed url from feed page: $baseUrl")
        return readUrl(httpClient, baseUrl)
            ?.let { html ->
                val doc = Ksoup.parseBodyFragment(html)
                    .selectFirst("div[class=description]")
                val description = doc?.text()
                val feedUrl = doc
                    ?.parent()
                    ?.childNodes
                    ?.let { nodes ->
                        val index = nodes.indexOfFirst { it is TextNode && it.text().contains("RSS-Feed-URL") }
                        if (index != -1 && index + 1 < nodes.size) {
                            val nextNode = nodes[index + 1]
                            if (nextNode is Element && nextNode.tagName() == "a") {
                                nextNode.attr("href")
                            } else null
                        } else null
                    }

                if (description != null && feedUrl != null) {
                    Pair(description, feedUrl)
                } else {
                    null
                }
            }
    }

    /**
     * Helper method to read text from an url using ok http client.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     */
    suspend fun readUrl(httpClient: HttpClient, url: String): String? {
        return try {
            val response = httpClient
                .get(urlString = url)
            response.bodyAsText()
        } catch (e: Exception) {
            log.e("Could not read from url '$url' [${e.message}]")
            null
        }
    }

    /**
     * Helper method to read text from an url using ok http client.
     *
     * @param httpClient The OkHttpClient to use for scraping.
     */
    suspend fun readUrlAsRawBytes(httpClient: HttpClient, url: String): ByteArray? {
        return try {
            val response = httpClient
                .get(urlString = url)
            response.readRawBytes()
        } catch (e: Exception) {
            log.e("Could not read from url '$url' [${e.message}]")
            null
        }
    }
}
