package de.visualdigits.newshomereader.data.util

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import de.visualdigits.newshomereader.domain.model.catalog.NewsCategory
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeed
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths
import java.time.OffsetDateTime
import kotlin.time.Duration.Companion.milliseconds

object CatalogScraper {

    private val log = kermitLogger(CatalogScraper::class)

    suspend fun scrapeToFile(httpClient: HttpClient, baseUrl: String) {
        log.i("Scraping catalog from baseUrl: $$baseUrl")
        val newsCategories = scrapeMainCategories(httpClient, baseUrl)
        val newsCatalog = NewsFeedCatalog(
            baseUrl = baseUrl,
            date = OffsetDateTime.now(),
            categories = newsCategories
        )

        val jsonMapper = Json {
            prettyPrint = true
        }
        val json = jsonMapper.encodeToString(newsCatalog)

        val directory = Paths.get(File(".").canonicalPath, "src/commonMain/resources").toFile()
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                log.e("Could not create catalog directory")
            }
        }
        val targetFile =
            File(directory, "catalog.json")
        targetFile.writeText(json)
    }

    private suspend fun scrapeMainCategories(
        httpClient: HttpClient,
        baseUrl: String
    ): List<NewsCategory> {
        log.i("Scraping toplevel categories")
        return readUrl(httpClient, baseUrl)
            ?.let { html ->
                Ksoup.parseBodyFragment(html).select("ul[class=verzeichnis level1] > li")
                    .map { elem ->
                        delay(2000.milliseconds) // anti anti scrape
                        val a = elem.selectFirst("> a")
                        val name = a?.attr("title") ?: ""
                        val categoryPageUrl = makeUrlAbsolute(baseUrl, a?.attr("href") ?: "")
                        NewsCategory(
                            name = name,
                            url = categoryPageUrl,
                            subCategories = scrapeSubCategories(
                                httpClient = httpClient,
                                rootUrl = baseUrl,
                                baseUrl = categoryPageUrl
                            )
                        )
                    }
            } ?: listOf()
    }

    private suspend fun scrapeSubCategories(
        httpClient: HttpClient,
        rootUrl: String,
        baseUrl: String
    ): List<NewsCategory> {
        log.i("  Scraping sub categories for category $baseUrl")
        return readUrl(httpClient, baseUrl)
            ?.let { html ->
                Ksoup.parseBodyFragment(html)
                    .select("ul[class=verzeichnis level2] > li")
                    .map {li ->
                        val a = li.select("a")
                        val subCategoryPageUrl = makeUrlAbsolute(baseUrl, a.attr("href"))
                        val categoryName = a.attr("title")
                        NewsCategory(
                            name = categoryName,
                            url = subCategoryPageUrl,
                            feeds = scrapeFeeds(
                                httpClient = httpClient,
                                rootUrl = rootUrl,
                                baseUrl = subCategoryPageUrl,
                                categoryName = categoryName
                            ),
                        )
                    }
            } ?: listOf()
    }

    private suspend fun scrapeFeeds(
        httpClient: HttpClient,
        rootUrl: String,
        baseUrl: String,
        categoryName: String
    ): List<NewsFeed> {
        log.i("    Scraping feeds from category '$categoryName'")

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

                val firstFeeds = scrapeCategoryPage(
                    httpClient = httpClient,
                    rootUrl = rootUrl,
                    baseUrl = baseUrl,
                    doc = doc,
                    categoryName = categoryName,
                    page = 1
                )
                val pageFeeds = pageDocs.flatMapIndexed { index, pageDoc ->
                    scrapeCategoryPage(
                        httpClient = httpClient,
                        rootUrl = rootUrl,
                        baseUrl = baseUrl,
                        doc = pageDoc,
                        categoryName = categoryName,
                        page = index + 2
                    )
                }

                firstFeeds + pageFeeds
            } ?: listOf()
    }

    private suspend fun scrapeCategoryPage(
        httpClient: HttpClient,
        rootUrl: String,
        baseUrl: String,
        doc: Document,
        categoryName: String,
        page: Int
    ): List<NewsFeed> {
        log.i("      Scraping feed page $page for category '$categoryName'")
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
                    NewsFeed(
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

    private suspend fun readUrl(httpClient: HttpClient, url: String): String? {
        delay(500.milliseconds) // anti anti scrape
        return try {
            httpClient
                .get(urlString = url)
                .bodyAsText()
        } catch (e: Exception) {
            log.e("Could not read from url: $url")
            null
        }
    }
}
