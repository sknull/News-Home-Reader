package de.visualdigits.newshomereader.data.model.rss

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.newshomereader.data.database.toNewsFeedEntity
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonDto
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File

@EnabledIf("isEnabled")
class RssTest : KoinTest {

    companion object {

        @JvmStatic
        fun isEnabled(): Boolean = false
    }

    private val feedRepository: FeedRepository by inject()
    private val articleRepository: ArticleRepository by inject()
    private val httpClient: HttpClient by inject()

    private val newsItem = NewsItem(
        id = 4711,
        link = "",
        feedName = "",
        identifier = "",
        published = KmpOffsetDateTime.now(),
        updated = KmpOffsetDateTime.now(),
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
    @Disabled("produces timeout")
    fun testReadFromUrl() = runTest {
        val response = httpClient.get(urlString = "https://www.focus.de/rss")
        val xml = response.bodyAsText()
        println(xml)
    }

    @Test
    fun testReadScript() {
        val json = File(ClassLoader.getSystemResource("newsfeed/rdf/script.json").toURI()).readText()
        val appJsonDto = AppJsonDto.decodeFromString(json)
        println(appJsonDto)
    }

    @Test
    fun testReadFeed() = runTest {
        feedRepository.refreshNewsFeed(
            feedName = "test",
            url = "https://t3n.de/rss.xml",
            wifiOnly = false,
            keepReadArticlesInDays = 30,
            keepUnreadArticlesInDays = 30,
            1200,
            loadArticles = false,
            progress = { _,_ -> }
        )
            .onSuccess { (newsFeed, _) ->
                val entity = newsFeed?.toNewsFeedEntity()
                println(entity)
                assertNotNull(entity)
            }
            .onError { _, throwable ->
                throwable?.also { throw it }
            }
    }

    @Test
    fun testReadArticleFile() = runTest {
        val article = readArticleFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/rdf/focus-story.html").toURI())
        )
        println(article)
        assertNotNull(article)
    }

    @Test
    @Disabled("url is outdated")
    fun testReadArticleUrl() = runTest {
        val response = httpClient.get(urlString = "https://www.spiegel.de/ausland/iran-krieg-us-senat-stimmt-dafuer-befugnisse-von-donald-trump-einzuschraenken-mit-republikaner-stimmen-a-12f9e1fa-16cf-4426-8b6c-39d72e5adcb6#ref=rss")
        val htmlRaw = response.bodyAsText()
        val article = articleRepository.readFromString(
            rawHtml = htmlRaw
        )
        println(article)
        assertNotNull(article)
    }

    @Test
    fun testReadYoutubeVideo() = runTest {
        val article = readArticleFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/rdf/nickyt-story.html").toURI())
        )
        println(article)
        assertNotNull(article)
    }

    @Test
    fun testReadYoutubeVideo2() = runTest {
        val article = readArticleFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/atom/heise-story-4.html").toURI())
        )
        println(article)
        assertNotNull(article)
    }

    @Test
    fun testReadArbeitstips() = runTest {
        val article = readArticleFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rss/arbeitstips-story.html").toURI()))
        println(article)
        assertNotNull(article)
    }

    @Test
    fun testReadNdr() = runTest {
        readFeedFromFile("ndr", File(ClassLoader.getSystemResource("newsfeed/rdf/ndr.xml").toURI()))
        val article = readArticleFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rdf/ndr-story.html").toURI()))
        assertNotNull(article)
    }

    @Test
    fun testReadNtv() = runTest {
        readFeedFromFile("ntv", File(ClassLoader.getSystemResource("newsfeed/rss/ntv.xml").toURI()))
        val article = readArticleFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rss/ntv-story.html").toURI()))
        assertNotNull(article)
    }

    @Test
    fun testReadT3n() = runTest {
        readFeedFromFile("t3n", File(ClassLoader.getSystemResource("newsfeed/rss/t3n.xml").toURI()))
        val article = readArticleFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/rss/t3n-story.html").toURI()))
        assertNotNull(article)
    }

    @Test
    fun testReadTOnline() = runTest {
        val feed = readFeedFromFile("t-online", File(ClassLoader.getSystemResource("newsfeed/rss/t-online.xml").toURI()))
        assertNotNull(feed)
    }

    @Test
    fun testReadTagesschau1() = runTest {
        readFeedFromFile("tagesschau", File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau.xml").toURI()))
        val article = readArticleFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau-story.html").toURI())
        )
        assertNotNull(article)
    }

    @Test
    fun testReadTagesschau2() = runTest {
        readFeedFromFile("tagesschau", File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau2.xml").toURI()))
        val article = readArticleFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau-story2.html").toURI())
        )
        assertNotNull(article)
    }

    @Test
    fun testReadTagesschau2a() = runTest {
        readFeedFromFile("tagesschau", File(ClassLoader.getSystemResource("newsfeed/rss/tagesschau2a.xml").toURI()))
    }

    @Test
    fun testReadHeise() = runTest {
        readFeedFromFile("heise", File(ClassLoader.getSystemResource("newsfeed/atom/heise.xml").toURI()))
        val article = readArticleFromFile(
            newsItem,
            File(ClassLoader.getSystemResource("newsfeed/atom/heise-story.html").toURI())
        )
        assertNotNull(article)
    }

    @Test
    fun testReadWdr() = runTest {
        readFeedFromFile("wdr", File(ClassLoader.getSystemResource("newsfeed/atom/wdr.xml").toURI()))
        val article = readArticleFromFile(newsItem, File(ClassLoader.getSystemResource("newsfeed/atom/wdr-story.html").toURI()))
        assertNotNull(article)
    }

    private suspend fun readArticleFromFile(
        newsItem: NewsItem,
        file: File
    ): FullArticle = withContext(Dispatchers.IO) {
        articleRepository.readFromString(newsItem, file.readText())
    }

    private suspend fun readFeedFromFile(
        feedName: String,
        file: File
    ): NewsFeed? = withContext(Dispatchers.IO) {
        feedRepository.readFromBytes(feedName, file.readBytes())
    }
}
