package de.visualdigits.newshomereader.data.model.rss

import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedEntity
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonDto
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.errorhandling.onSuccess
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File

@Disabled("Only for local testing")
class RssTest : KoinTest {

    private val feedService: FeedRepository by inject()
    private val fullArticleService: ArticleRepository by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(sharedModule, platformModule)
//        modules(sharedModule, testModule, platformModule)
    }

    @Test
    fun testReadScript() {
        val json = File(ClassLoader.getSystemResource("rdf/script.json").toURI()).readText()
        val appJsonDto = AppJsonDto.decodeFromString(json)
        println(appJsonDto)
    }

    @Test
    fun testReadArticleUrl() {
        runBlocking {
            val article = fullArticleService.readFullArticle(
                4711,
                "https://www.heise.de/bestenlisten/testbericht/ecovacs-t90-pro-omni-im-test-saugroboter-saugt-stark-und-laedt-klug/j1tydeh?wt_mc=rss.red.ho.ho.atom.beitrag.beitrag"
            )
                .onSuccess { article ->
                    println(article)
                }.onError { remote, throwable -> throwable?.also { throw it } }
        }
    }

    @Test
    fun testReadFeed() {
        runBlocking {
            feedService.refreshNewsFeed(
                feedName = "test",
                url = "https://t3n.de/rss.xml",
                keepReadArticlesInDays = 30,
                keepUnreadArticlesInDays = 30,
                loadArticles = false
            ) {}
                .onSuccess { newsFeed ->
                    val entity = newsFeed?.toNewsFeedEntity()
                    println(entity)
                }
                .onError { remote, throwable ->
                    throwable?.also { throw it }
                }
        }
    }

    @Test
    fun testReadArticleFile() {
        runBlocking {
            val article = fullArticleService.readFromFile(
                4711,
                File(ClassLoader.getSystemResource("rdf/heise-story-2.html").toURI())
            )
            println(article)
        }
    }

    @Test
    fun testReadModel() {
        runBlocking {
            feedService.readFromFile("heise", File(ClassLoader.getSystemResource("rdf/heise.xml").toURI()))
            fullArticleService.readFromFile(4711, File(ClassLoader.getSystemResource("rdf/heise-story.html").toURI()))

            feedService.readFromFile("ndr", File(ClassLoader.getSystemResource("rdf/ndr.xml").toURI()))
            fullArticleService.readFromFile(4711, File(ClassLoader.getSystemResource("rdf/ndr-story.html").toURI()))

            feedService.readFromFile("ntv", File(ClassLoader.getSystemResource("rdf/ntv.xml").toURI()))
            fullArticleService.readFromFile(4711, File(ClassLoader.getSystemResource("rdf/ntv-story.html").toURI()))

            feedService.readFromFile("t3n", File(ClassLoader.getSystemResource("rdf/t3n.xml").toURI()))
            fullArticleService.readFromFile(4711, File(ClassLoader.getSystemResource("rdf/t3n-story.html").toURI()))

            feedService.readFromFile("t-online", File(ClassLoader.getSystemResource("rdf/t-online.xml").toURI()))

            feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau.xml").toURI()))
            fullArticleService.readFromFile(
                4711,
                File(ClassLoader.getSystemResource("rdf/tagesschau-story.html").toURI())
            )

            feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau2.xml").toURI()))
            fullArticleService.readFromFile(
                4711,
                File(ClassLoader.getSystemResource("rdf/tagesschau-story2.html").toURI())
            )

            feedService.readFromFile("tagesschau", File(ClassLoader.getSystemResource("rdf/tagesschau2a.xml").toURI()))

            feedService.readFromFile("wdr", File(ClassLoader.getSystemResource("rdf/wdr.xml").toURI()))
            fullArticleService.readFromFile(4711, File(ClassLoader.getSystemResource("rdf/wdr-story.html").toURI()))
        }

        println()
    }
}
