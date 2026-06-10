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
import kotlinx.coroutines.test.runTest
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
        val json = File(ClassLoader.getSystemResource("newsfeed/rdf/script.json").toURI()).readText()
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
    fun testReadArticleFile() {
        runBlocking {
            val article = fullArticleService.readFromFile(
                newsItem,
                File(ClassLoader.getSystemResource("newsfeed/rdf/focus-story.html").toURI())
            )
            println(article)
        }
    }

    @Test
    @Disabled("Only for local testing")
    fun testReadArticleUrl() {
        runBlocking {
            val response = httpClient.get(urlString = "https://www.spiegel.de/ausland/iran-krieg-us-senat-stimmt-dafuer-befugnisse-von-donald-trump-einzuschraenken-mit-republikaner-stimmen-a-12f9e1fa-16cf-4426-8b6c-39d72e5adcb6#ref=rss")
            val htmlRaw = response.bodyAsText()
            val article = fullArticleService.readFromString(
                rawHtml = htmlRaw
            )
            println(article)
        }
    }

    @Test
    fun testReadYoutubeVideo() {
        runBlocking {
            val article = fullArticleService.readFromFile(
                newsItem,
                File(ClassLoader.getSystemResource("newsfeed/rdf/nickyt-story.html").toURI())
            )
            println(article)
        }
    }

    @Test
    fun testReadArbeitstips() = runTest {
        val article = fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rss/arbeitstips-story.html").toURI()))
        println(article)
    }

    @Test
    fun testReadNdr() = runTest {
        feedService.readFromFile("ndr", File(ClassLoader.getSystemResource("newsfeed/rdf/ndr.xml").toURI()))
        fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rdf/ndr-story.html").toURI()))
    }

    @Test
    fun testReadNtv() = runTest {
        feedService.readFromFile("ntv", File(ClassLoader.getSystemResource("newsfeed/rss/ntv.xml").toURI()))
        fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rss/ntv-story.html").toURI()))
    }

    @Test
    fun testReadT3n() = runTest {
        feedService.readFromFile("t3n", File(ClassLoader.getSystemResource("newsfeed/rss/t3n.xml").toURI()))
        fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rss/t3n-story.html").toURI()))
    }

    @Test
    fun testReadTOnline() = runTest {
        feedService.readFromFile("t-online", File(ClassLoader.getSystemResource("newsfeed/rss/t-online.xml").toURI()))
    }

    @Test
    fun testReadTagesschau1() = runTest {
        feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau.xml").toURI()))
        fullArticleService.readFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau-story.html").toURI())
        )
    }

    @Test
    fun testReadTagesschau2() = runTest {
        feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau2.xml").toURI()))
        fullArticleService.readFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau-story2.html").toURI())
        )
    }

    @Test
    fun testReadTagesschau2a() = runTest {
        feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau2a.xml").toURI()))
    }

    @Test
    fun testReadHeise() = runTest {
        feedService.readFromFile("heise", File(ClassLoader.getSystemResource("newsfeed/atom/heise.xml").toURI()))
        fullArticleService.readFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/atom/heise-story.html").toURI())
        )
    }

    @Test
    fun testReadWdr() = runTest {
        feedService.readFromFile("wdr", File(ClassLoader.getSystemResource("newsfeed/atom/wdr.xml").toURI()))
        fullArticleService.readFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/atom/wdr-story.html").toURI()))
    }
}
