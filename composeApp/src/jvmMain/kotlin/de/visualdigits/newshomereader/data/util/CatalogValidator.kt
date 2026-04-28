package de.visualdigits.newshomereader.data.util

import de.visualdigits.newshomereader.data.util.CatalogScraper.readUrlAsRawBytes
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object CatalogValidator {

    private val log = kermitLogger(CatalogScraper::class)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * Validates a given catalog by parsing all feeds once and
     * determining the latest update from the feed itself and
     * the feed items as some feeds pretend to be currently updated
     * while only very old news item exist.
     * Feeds which have not been updated with 90 days or do not
     * deliver a feed or are unreachable are sorted out in sake for
     * having clean news feed catalog which only contains actively
     * maintained feeds.
     * The validation results are also output to a validation output file
     * as this is along running job and something could go wrong.
     *
     * @param httpClient The OkHttpClient to use for reading the feeds.
     * @param feedRepository The feed repository which actually reads and normalizes the feeds
     * @param catalogFile The catalog file to validate
     * @param targetFile THe target catalog file to produce
     * @param validationOutputFile The output file for later use
     */
    fun validateCatalog(
        httpClient: HttpClient,
        feedRepository: FeedRepository,
        catalogFile: File,
        targetFile: File,
        validationOutputFile: File
    ) {
        runBlocking {
            val thresholdDate = OffsetDateTime.now().minus(90, ChronoUnit.DAYS)
            val newsFeedCatalog = Json.decodeFromString(NewsFeedCatalog.serializer(), catalogFile.readText())
            val mainCategories = newsFeedCatalog.categories.mapNotNull { mainCategory ->
                output(validationOutputFile, mainCategory.name)
                val subCategories = mainCategory.subCategories.mapNotNull { subCategory ->
                    output(validationOutputFile, "  ${subCategory.name}")
                    val feeds = subCategory.feeds.mapNotNull { feed ->
                        try {
                            val bytes = readUrlAsRawBytes(httpClient, feed.url)
                            val newsFeed = feedRepository.readFromBytes(feed.name, bytes) // contains logic to read and normalize diverse feeds of type rss, rdf and atom
                            if (newsFeed != null) {
                                val updated = newsFeed.updated
                                if (updated.isBefore(thresholdDate)) {
                                    output(validationOutputFile, "    ${feed.name}: OUTDATED FEED")
                                    null
                                } else if (newsFeed.items.maxBy { item -> item.updated }.updated.isBefore(thresholdDate)) {
                                    output(validationOutputFile, "    ${feed.name}: OUTDATED NEWS ITEMS")
                                    null
                                } else if (newsFeed.items.isEmpty()) {
                                    output(validationOutputFile, "    ${feed.name}: EMPTY FEED")
                                    null
                                } else {
                                    output(validationOutputFile, "    ${feed.name}: ${updated.format(formatter)}")
                                    feed
                                }
                            } else {
                                output(validationOutputFile, "    ${feed.name}: UNSUPPORTED FORMAT")
                                null
                            }
                        } catch (e: Exception) {
                            output(validationOutputFile, "    ${feed.name}: UNREACHABLE [${e.message}]")
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
            val jsonMapper = Json {
                prettyPrint = true
            }
            val json = jsonMapper.encodeToString(validatedCatalog)
            targetFile.writeText(json)
        }
    }

    /**
     * Creates a markdown report from a given validation output
     * with sorted lists.
     *
     * @param validationOutputFile The output file for later use
     */
    fun createValidationReport(
        validationOutputFile: File,
        targetFile: File
    ) {
        targetFile.writeText("")
        val validationMap = createValidationMap(validationOutputFile)
        validationMap.toSortedMap().forEach { mainCategory, map ->
            targetFile.appendText("# $mainCategory\n\n")
            map.toSortedMap().forEach { subCategory, map ->
                targetFile.appendText("## $subCategory\n\n")
                map.toSortedMap().forEach { feedName, status ->
                    targetFile.appendText("- $feedName: $status\n")
                }
                targetFile.appendText("\n")
            }
        }
    }

    /**
     * When something goes wrong during validation (which takes about an hour currently)
     * one can still apply the validation results to a given catalog.
     *
     * @param validationOutputFile The output file for later use
     */
    fun applyValidationToCatalog(
        catalogFile: File,
        validationOutputFile: File,
        targetFile: File,
    ) {
        val validationMap = createValidationMap(validationOutputFile)
        val newsFeedCatalog = Json.decodeFromString(NewsFeedCatalog.serializer(), catalogFile.readText())
        val mainCategories = newsFeedCatalog.categories.mapNotNull { mainCategory ->
            output(validationOutputFile, mainCategory.name)
            val subCategories = mainCategory.subCategories.mapNotNull { subCategory ->
                output(validationOutputFile, "  ${subCategory.name}")
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
        val jsonMapper = Json {
            prettyPrint = true
        }
        val json = jsonMapper.encodeToString(validatedCatalog)
        targetFile.writeText(json)
    }

    /**
     * Creates a lookup map from a given validation output file
     *
     * @param validationOutputFile The output file for later use
     */
    private fun createValidationMap(
        validationOutputFile: File
    ): Map<String, Map<String, Map<String, String>>> {
        val validationMap = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
        var mainCategory = ""
        var subCategory = ""
        validationOutputFile
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

    /**
     * Wrapper to log messages and write them into a output file for later use.
     *
     * @param outputFile The file to append to
     * @param message The messgae to output
     */
    private fun output(
        outputFile: File,
        message: String
    ) {
        log.i(message)
        outputFile.appendText("$message\n")
    }
}
