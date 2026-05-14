package de.visualdigits.newshomereader.data.model.rss

import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.newshomereader.data.database.toNewsFeedEntity
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonDto
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File
import java.time.OffsetDateTime

class RssTest : KoinTest {

    private val feedService: FeedRepository by inject()
    private val fullArticleService: ArticleRepository by inject()
    private val httpClient: HttpClient by inject()

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
        modules(sharedModule, platformModule)
//        modules(sharedModule, testModule, platformModule)
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadFromUrl() {
        runBlocking {
            val response = httpClient.get(urlString = "https://www.focus.de/rss")
            val xml = response.bodyAsText()
            println(xml)
        }
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadScript() {
        val json = File(ClassLoader.getSystemResource("rdf/script.json").toURI()).readText()
        val appJsonDto = AppJsonDto.decodeFromString(json)
        println(appJsonDto)
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadFeed() {
        runBlocking {
            feedService.refreshNewsFeed(
                feedName = "test",
                url = "https://t3n.de/rss.xml",
                wifiOnly = false,
                keepReadArticlesInDays = 30,
                keepUnreadArticlesInDays = 30,
                1200,
                loadArticles = false,
                progress = { _,_ -> }
            )
                .onSuccess { (newsFeed, changed) ->
                    val entity = newsFeed?.toNewsFeedEntity()
                    println(entity)
                }
                .onError { _, throwable ->
                    throwable?.also { throw it }
                }
        }
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadArticleFile() {
        runBlocking {
            val article = fullArticleService.readFromFile(
                newsItem,
                File(ClassLoader.getSystemResource("rdf/focus-story.html").toURI())
            )
            println(article)
        }
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadArticleUrl() {
        runBlocking {
            val response = httpClient.get(urlString = "https://www.focus.de/panorama/welt/schadet-nicht-privat-vorzusorgen-mathematiker-investiert-monatlich-1500-euro-in-etfs_daf719c6-c9c1-4aaf-8cbf-670075509e12.html")
            val htmlRaw = response.bodyAsText()
            val article = fullArticleService.readFromString(
                rawHtml = htmlRaw
            )
            println(article)
        }
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadModel() {
        runBlocking {
            feedService.readFromFile("heise", File(ClassLoader.getSystemResource("rdf/heise.xml").toURI()))
            fullArticleService.readFromFile(
                newsItem,
                File(ClassLoader.getSystemResource("rdf/heise-story.html").toURI())
            )

            feedService.readFromFile("ndr", File(ClassLoader.getSystemResource("rdf/ndr.xml").toURI()))
            fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("rdf/ndr-story.html").toURI()))

            feedService.readFromFile("ntv", File(ClassLoader.getSystemResource("rdf/ntv.xml").toURI()))
            fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("rdf/ntv-story.html").toURI()))

            feedService.readFromFile("t3n", File(ClassLoader.getSystemResource("rdf/t3n.xml").toURI()))
            fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("rdf/t3n-story.html").toURI()))

            feedService.readFromFile("t-online", File(ClassLoader.getSystemResource("rdf/t-online.xml").toURI()))

            feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau.xml").toURI()))
            fullArticleService.readFromFile(
                newsItem,
                File(ClassLoader.getSystemResource("rdf/tagesschau-story.html").toURI())
            )

            feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau2.xml").toURI()))
            fullArticleService.readFromFile(
                newsItem,
                File(ClassLoader.getSystemResource("rdf/tagesschau-story2.html").toURI())
            )

            feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau2a.xml").toURI()))

            feedService.readFromFile("wdr", File(ClassLoader.getSystemResource("rdf/wdr.xml").toURI()))
            fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("rdf/wdr-story.html").toURI()))
        }

        println()
    }
}
