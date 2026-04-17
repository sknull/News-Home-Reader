package de.visualdigits.newshomereader.repository

import de.visualdigits.newshomereader.data.mapper.toNewsFeed
import de.visualdigits.newshomereader.data.model.atom.Feed
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.data.model.rss.Rss
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.io.File

class MockFeedRepository : FeedRepository {

    override suspend fun readFromFile(
        feedName: String,
        file: File
    ): NewsFeed {
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

    override suspend fun upsertNewsItem(newsItem: NewsItem, forceUpdate: Boolean): Result<Unit, DataError.Local> {
        return Result.Success(Unit)
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
    ): Result<NewsFeed?, DataError.Remote> {
        return Result.Success(readFromString(feedName, ""))
    }

    override suspend fun readFromString(
        feedName: String,
        xml: String?
    ): NewsFeed {
        checkNotNull(xml) { "No xml given" }

        val feedType = Jsoup
            .parse(xml, "", Parser.xmlParser())
            .root()
            .select("> *")
            .firstOrNull()
            ?.tagName()
            ?.split(":")
            ?.firstOrNull()
            ?.lowercase()

        return when (feedType) {
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
