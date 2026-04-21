package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.di.testModule
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.errorhandling.onSuccess
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.time.OffsetDateTime

//@Disabled("Only for local testing")
class ArticleRepositoryTest : KoinTest {

    private val repository: ArticleRepository by inject()

    private val newsItem = NewsItem(
        id = 4711,
        link = "",
        feedName = "",
        identifier = "",
        published = OffsetDateTime.now(),
        updated = OffsetDateTime.now(),
        title = "",
        summary = "",
        content = "",
        keywords = listOf(),
        image = "",
        imageTitle = "",
        imageCaption = "",
        isRead = false,
        newsFeed = null,
        newsArticle = null,
    )

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, testModule)
    }

    @Test
    fun testReadArticle() {
        runBlocking {
            repository.readFullArticle(newsItem.copy(link = "https://www1.wdr.de/nachrichten/olympia-abstimmung-nrw-koeln-rhein-ruhr-ticker-100.html"))
                .onSuccess { article ->
                    println(article)
                }.onError { _, throwable ->
                    if (throwable != null) {
                        throw throwable
                    }
                }
        }
    }

    @Test
    fun testReadArticleUrl() {
        runBlocking {
            repository.readFullArticle(newsItem.copy(link = "https://www.heise.de/bestenlisten/testbericht/ecovacs-t90-pro-omni-im-test-saugroboter-saugt-stark-und-laedt-klug/j1tydeh?wt_mc=rss.red.ho.ho.atom.beitrag.beitrag"))
                .onSuccess { article ->
                    println(article)
                }.onError { _, throwable -> throwable?.also { throw it } }
        }
    }
}
