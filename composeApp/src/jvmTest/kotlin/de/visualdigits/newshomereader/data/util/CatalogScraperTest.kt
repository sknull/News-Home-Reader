package de.visualdigits.newshomereader.data.util

import de.visualdigits.newshomereader.data.util.CatalogScraper.readUrl
import de.visualdigits.newshomereader.data.util.CatalogScraper.scrapeToFile
import de.visualdigits.newshomereader.data.util.CatalogValidator.applyValidationToCatalog
import de.visualdigits.newshomereader.data.util.CatalogValidator.createValidationReport
import de.visualdigits.newshomereader.data.util.CatalogValidator.validateCatalog
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File
import java.nio.file.Paths

class CatalogScraperTest : KoinTest {

    private val httpClient: HttpClient by inject()
    private val feedRepository: FeedRepository by inject()

    private val originalCatalogFile = Paths.get(File(".").canonicalPath, "src/jvmTest/resources/catalog/catalog_original.json").toFile()
    private val validatedCatalogFile = Paths.get(File(".").canonicalPath, "src/jvmTest/resources/catalog/catalog_validated.json").toFile()

    private val validationOutputFile = Paths.get(File(".").canonicalPath, "src/jvmTest/resources/catalog/catalog-validation-output.txt").toFile()
    private val reportFile = Paths.get(File(".").canonicalPath, "src/jvmTest/resources/catalog/catalog-validation-report.md").toFile()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
    }

    /**
     * Prints out a report from the validation output.
     */
    @Test
    @Disabled("Only for manual execution")
    fun testCreateValidationReport() {
        createValidationReport(
            validationOutputFile = validationOutputFile,
            targetFile = reportFile
        )
    }

    /**
     * Applies the validation output and creates a cleanedup copy of the catalog.
     */
    @Test
    @Disabled("Only for manual execution")
    fun testApplyValidationToCatalog() {
        applyValidationToCatalog(
            catalogFile = originalCatalogFile,
            validationOutputFile = validationOutputFile,
            targetFile = validatedCatalogFile
        )
    }

    @Test
    @Disabled("Only for manual execution")
    fun testReadFeed() {
        runBlocking {
            val xml = readUrl(httpClient, "https://feeds.feedburner.com/blogspot/rkEL")
            val newsFeed = feedRepository.readFromString("TEST", xml)
            println(newsFeed)
        }
    }

    @Test
    @Disabled("Only for manual execution")
    fun testValidate() {
        validateCatalog(
            httpClient = httpClient,
            feedRepository = feedRepository,
            catalogFile = originalCatalogFile,
            targetFile = validatedCatalogFile,
            validationOutputFile = validationOutputFile
        )
    }

    @Test
    @Disabled("Only for manual execution")
    fun testScrape() {
        runBlocking {
            val categories = scrapeToFile(
                httpClient = httpClient
            )
            println(categories)
        }
    }
}
