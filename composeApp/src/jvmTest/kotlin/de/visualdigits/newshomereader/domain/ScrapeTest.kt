package de.visualdigits.newshomereader.domain

import de.visualdigits.newshomereader.data.util.CatalogScraper.scrapeToFile
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@Disabled("Only for manual execution")
class ScrapeTest : KoinTest {

    private val httpClient: HttpClient by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
    }

    @Test
    fun testScrape() {
        runBlocking {
            val categories = scrapeToFile(httpClient, "https://www.rss-verzeichnis.de")
            println(categories)
        }
    }
}
