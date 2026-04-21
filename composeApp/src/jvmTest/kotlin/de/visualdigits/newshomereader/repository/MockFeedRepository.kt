package de.visualdigits.newshomereader.repository

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.parser.Parser
import de.visualdigits.newshomereader.data.mapper.toNewsFeed
import de.visualdigits.newshomereader.data.model.atom.Feed
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.data.model.rss.Rss
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import java.io.File

class MockFeedRepository(
    private val httpClient: HttpClient,
) : FeedRepository {

    override suspend fun readFromFile(
        feedName: String,
        file: File
    ): NewsFeed? {
        return readFromString(feedName, file.readText())
    }

    override suspend fun getFeedItemsByNewsFeedName(feedName: String): Result<List<NewsItem>, DataError.Remote> {
        return Result.Success(listOf())
    }

    override fun observeFeedItems(feedName: String): Flow<List<NewsItem>> {
        return emptyFlow()
    }

    override suspend fun upsertNewsFeed(newsFeed: NewsFeed): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
    }

    override suspend fun markNewsItemsAsRead(ids: List<Long>): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
    }

    override suspend fun upsertNewsItem(newsItem: NewsItem, forceUpdate: Boolean): Result<Pair<NewsItem, Boolean>, DataError.Local> {
        return Result.Success(Pair(newsItem, false))
    }

    override suspend fun refreshNewsFeeds(
        newsFeedConfigurations: List<NewsFeedConfigurationEntity>,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float) -> Unit
    ): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> {
        return Result.Success(Pair(listOf(), false))
    }

    override suspend fun refreshNewsFeed(
        feedName: String,
        url: String,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float) -> Unit
    ): Result<Pair<NewsFeed?, Boolean>, DataError.Remote> {
        val response = httpClient.get(urlString = url)
        val xml = response.bodyAsText()
        return Result.Success(Pair(readFromString(feedName, xml), false))
    }

    override suspend fun readFromString(
        feedName: String?,
        xml: String?
    ): NewsFeed? = withContext(Dispatchers.IO) {
        checkNotNull(xml) { "No xml given" }

        val feedType = Ksoup
            .parse(html = xml, baseUri = "", parser = Parser.xmlParser())
            .root()
            .select("> *")
            .firstOrNull()
            ?.tagName()
            ?.split(":")
            ?.firstOrNull()
            ?.lowercase()

        when (feedType) {
            "rss" -> {
                val rss = decodeFromString<Rss>(xml)
                rss.toNewsFeed(feedName)
            }
            "rdf" -> {
                val rdf = decodeFromString<Rdf>(xml)
                rdf.toNewsFeed(feedName)
            }
            "feed" -> {
                val feed = decodeFromString<Feed>(xml)
                feed.toNewsFeed(feedName)
            }
            else -> error("Unsupported feed type '$feedType'")
        }
    }
}
