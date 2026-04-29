package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.di.testModule
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

class FeedRepositoryTest : KoinTest {

    private val repository: FeedRepository by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, testModule)
    }

    @Test
    fun testReadFeed() {
        runBlocking {
            repository.refreshNewsFeed(
                feedName = "TEST", url = "https://trancefertohamburg.ddns.net/rss.xml",
                wifiOnly = false,
                keepReadArticlesInDays = 30,
                keepUnreadArticlesInDays = 30,
                maxImageSize = 1200,
                loadArticles = false,
                progress = { _,_ -> },
            )
                .onSuccess { feed ->
                    println(feed)
                }.onError { _, throwable ->
                    if (throwable != null) {
                        throw throwable
                    }
                }
        }
    }
}
