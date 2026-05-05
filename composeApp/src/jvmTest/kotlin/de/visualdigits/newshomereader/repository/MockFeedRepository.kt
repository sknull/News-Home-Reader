package de.visualdigits.newshomereader.repository

import com.fleeksoft.ksoup.Ksoup
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.data.mapper.toNewsFeed
import de.visualdigits.newshomereader.data.model.atom.Feed
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.data.model.rss.Rss
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.Dispatchers
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
        return readFromBytes(feedName, file.readBytes())
    }

    override suspend fun getFeedItemsByNewsFeedName(feedName: String): Result<List<NewsItem>, DataError.Remote> {
        return Result.Success(listOf())
    }

    override fun observeFeedItems(newsFeedGroup: NewsFeedGroup?, newsFeedName: String?): Flow<List<NewsItem>> {
        return emptyFlow()
    }

    override suspend fun upsertNewsFeed(newsFeed: NewsFeed): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
    }

    override suspend fun markNewsItemsAsRead(newsItems: List<NewsItem>): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
    }

    override suspend fun synchroniseReadNewsItems(): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
    }

    override suspend fun upsertNewsItem(newsItem: NewsItem, forceUpdate: Boolean): Result<Pair<NewsItem, Boolean>, DataError.Local> {
        return Result.Success(Pair(newsItem, false))
    }

    override suspend fun refreshNewsFeeds(
        newsFeedItems: List<NewsFeedItem>,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float, ProgressStage) -> Unit
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
        progress: (Float, ProgressStage) -> Unit
    ): Result<Pair<NewsFeed?, Boolean>, DataError.Remote> {
        val response = httpClient.get(urlString = url)
        return Result.Success(Pair(readFromBytes(feedName, response.readRawBytes()), false))
    }

    override suspend fun prefetchImages(
        newsFeeds: List<NewsFeed>,
        progress: (Float, ProgressStage) -> Unit
    ): Result<Unit, DataError.Remote> {
        return Result.Success(Unit)
    }


    override suspend fun readFromBytes(
        feedName: String?,
        bytes: ByteArray?
    ): NewsFeed? = withContext(Dispatchers.IO) {
        checkNotNull(feedName) { "No feed name given" }
        checkNotNull(bytes) { "No xml given" }
        val head = bytes.take(200).toByteArray().decodeToString()
        val charsetName = Regex("""encoding=["'](.*?)["']""").find(head)?.groupValues?.get(1) ?: "UTF-8"
        val xml = bytes.toString(charset(charsetName))

        val feedType = Ksoup
            .parse(html = xml, baseUri = "", parser = com.fleeksoft.ksoup.parser.Parser.xmlParser())
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
            else -> {
                null // Unsupported feed type
            }
        }
    }
}
