package de.visualdigits.newshomereader.data.util

import de.visualdigits.newshomereader.data.util.CatalogScraper.readUrl
import de.visualdigits.newshomereader.data.util.CatalogScraper.scrapeToFile
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CatalogScraperTest : KoinTest {

    private val httpClient: HttpClient by inject()
    private val feedRepository: FeedRepository by inject()

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val directory = Paths.get(File(".").canonicalPath, "src/commonMain/composeResources/files").toFile()
    private val catalogFile = File(directory, "catalog.json")

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
    }

    @Test
    fun validationReport() {
        val validationMap = createValidationMap()
        validationMap.toSortedMap().forEach { mainCategory, map ->
            println("# $mainCategory\n")
            map.toSortedMap().forEach { subCategory, map ->
                println("## $subCategory\n")
                map.toSortedMap().forEach { feedName, status ->
                    println("- $feedName: $status")
                }
                println()
            }
        }
    }

    @Test
    @Disabled("Only for manual execution")
    fun applyValidationToCatalog() {
        /*
         * Only used after writing results failed due to missing transient annotation.
         */
        val validationMap = createValidationMap()
        val newsFeedCatalog = Json.decodeFromString(NewsFeedCatalog.serializer(), catalogFile.readText())
        val mainCategories = newsFeedCatalog.categories.mapNotNull { mainCategory ->
            println(mainCategory.name)
            val subCategories = mainCategory.subCategories.mapNotNull { subCategory ->
                println("  ${subCategory.name}")
                val feeds = subCategory.feeds.mapNotNull { feed ->
                    val status = validationMap[mainCategory.name]?.get(subCategory.name)?.get(feed.name)
                    if (status != "OUTDATED FEED" && status != "OUTDATED ITEMS" && status != "UNSUPPORTED FORMAT" && status?.startsWith("UNREACHABLE") == false) {
                        feed
                    } else {
                        null
                    }
                }
                if (feeds.isNotEmpty()) {
                    subCategory.copy(feeds = feeds.sortedBy { f -> f.name })
                } else {
                    null
                }
            }
            if (subCategories.isNotEmpty()) {
                mainCategory.copy(subCategories = subCategories.sortedBy { f -> f.name })
            } else {
                null
            }
        }
        val validatedCatalog = newsFeedCatalog.copy(categories = mainCategories.sortedBy { f -> f.name })
        val targetFile = File(directory, "catalog_validated.json")
        val jsonMapper = Json {
            prettyPrint = true
        }
        val json = jsonMapper.encodeToString(validatedCatalog)
        targetFile.writeText(json)
    }

    private fun createValidationMap(): Map<String, Map<String, Map<String, String>>> {
        val validationMap = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
        var mainCategory = ""
        var subCategory = ""
        File(ClassLoader.getSystemResource("catalog-validation-output.txt").toURI())
            .readLines()
            .forEach { line ->
                when {
                    line.startsWith("    ") -> {
                        if (mainCategory.isNotEmpty() && subCategory.isNotEmpty()) {
                            val name = line.substringBeforeLast(":")
                            val status = line.substringAfterLast(":")
                            val map = validationMap.computeIfAbsent(mainCategory) { mutableMapOf() }
                            val subMap = map.computeIfAbsent(subCategory) { mutableMapOf() }
                            subMap[name] = status
                        }
                    }

                    line.startsWith("  ") -> {
                        subCategory = line.trim()
                    }

                    else -> {
                        mainCategory = line.trim()
                    }
                }
            }
        return validationMap
    }

    @Test
    fun testReadFeed() {
        runBlocking {
            val xml = readUrl(httpClient, "https://feeds.feedburner.com/blogspot/rkEL")
            val newsFeed = feedRepository.readFromString("TEST", xml)
            println(newsFeed)
        }
    }

    @Test
//    @Disabled("Only for manual execution")
    fun validateCatalog() {
        runBlocking {
            val thresholdDate = OffsetDateTime.now().minus(90, ChronoUnit.DAYS)
            val newsFeedCatalog = Json.decodeFromString(NewsFeedCatalog.serializer(), catalogFile.readText())
            val mainCategories = newsFeedCatalog.categories.mapNotNull { mainCategory ->
                println(mainCategory.name)
                val subCategories = mainCategory.subCategories.mapNotNull { subCategory ->
                    println("  ${subCategory.name}")
                    val feeds = subCategory.feeds.mapNotNull { feed ->
                        try {
                            val xml = readUrl(httpClient, feed.url)
                            val newsFeed = feedRepository.readFromString(feed.name, xml)
                            if (newsFeed != null) {
                                val updated = newsFeed.updated
                                if (updated.isBefore(thresholdDate)) {
                                    println("    ${feed.name}: OUTDATED FEED")
                                    null
                                } else if (newsFeed.items.maxBy { item -> item.updated }.updated.isBefore(thresholdDate)) {
                                    println("    ${feed.name}: OUTDATED NEWS ITEMS")
                                    null
                                } else if (newsFeed.items.isEmpty()) {
                                    println("    ${feed.name}: EMPTY FEED")
                                    null
                                } else {
                                    println("    ${feed.name}: ${updated.format(formatter)}")
                                    feed
                                }
                            } else {
                                println("    ${feed.name}: UNSUPPORTED FORMAT")
                                null
                            }
                        } catch (e: Exception) {
                            println("    ${feed.name}: UNREACHABLE [${e.message}]")
                            null
                        }
                    }
                    if (feeds.isNotEmpty()) {
                        subCategory.copy(feeds = feeds)
                    } else {
                        null
                    }
                }
                if (subCategories.isNotEmpty()) {
                    mainCategory.copy(subCategories = subCategories)
                } else {
                    null
                }
            }
            val validatedCatalog = newsFeedCatalog.copy(categories = mainCategories)
            val targetFile = File(directory, "catalog_validated.json")
            val jsonMapper = Json {
                prettyPrint = true
            }
            val json = jsonMapper.encodeToString(validatedCatalog)
            targetFile.writeText(json)
        }
    }

    @Test
    @Disabled("Only for manual execution")
    fun testScrape() {
        runBlocking {
            val categories = scrapeToFile(httpClient, "https://www.rss-verzeichnis.de")
            println(categories)
        }
    }
}
