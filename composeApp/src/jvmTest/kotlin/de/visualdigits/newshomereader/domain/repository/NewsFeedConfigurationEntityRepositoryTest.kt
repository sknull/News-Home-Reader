package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File

@Disabled("Only for local testing")
class NewsFeedConfigurationEntityRepositoryTest : KoinTest {

    private val repository: NewsFeedConfigurationRepository by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        // Hier deine echten oder Mock-Module laden
        modules(sharedModule, platformModule)
    }

    @Test
    fun testWriteNewsConfig() {
        val json = File(ClassLoader.getSystemResource("newsfeeds/newsfeeds.json").toURI()).readText()
        val newsFeeds = NewsFeedConfigurationEntity.decodeFromString(json)

        runBlocking {
            repository.setNewsFeeds(newsFeeds)
        }

        println(newsFeeds)
    }
}
