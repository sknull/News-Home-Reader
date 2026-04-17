package de.visualdigits.newshomereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeed
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedEntity
import de.visualdigits.newshomereader.data.database.mapper.toNewsItem
import de.visualdigits.newshomereader.data.database.mapper.toNewsItemEntity
import de.visualdigits.newshomereader.data.database.upsertNewsFeed
import de.visualdigits.newshomereader.data.database.upsertNewsItem
import de.visualdigits.newshomereader.data.mapper.toNewsFeed
import de.visualdigits.newshomereader.data.model.atom.Feed
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.data.model.rss.Rss
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.io.File
import java.time.Duration
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger

class DefaultFeedRepository(
    private val httpClient: HttpClient? = null,
    private val dao: NewsHomeReaderDatabaseQueries,
    private val connectivityManager: ConnectivityManager,
    private val imageCache: ImageCache,
    val articleRepository: ArticleRepository,
) : FeedRepository {

    override suspend fun readFromFile(
        feedName: String,
        file: File
    ): NewsFeed = withContext(Dispatchers.IO) {
        readFromString(feedName, file.readText())
    }

    override suspend fun getFeedItemsByNewsFeedName(feedName: String): Result<List<NewsItem>, DataError.Remote> = withContext(Dispatchers.IO) {
        try {
            val data = dao.getAllNewsItemsByFeedName(feedName.trim().lowercase())
                .executeAsList()
                .map { ni ->
                    ni.toNewsItem()
                }
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }

    override fun observeFeedItems(feedName: String): Flow<List<NewsItem>> {
        return dao.getAllNewsItemsByFeedName(feedName.trim().lowercase())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { it.toNewsItem() }
            }
    }

    override suspend fun upsertNewsFeed(newsFeed: NewsFeed): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            dao.upsertNewsFeed(newsFeed.toNewsFeedEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun markNewsItemsAsRead(ids: List<Long>): Result<Unit, DataError.Local> = withContext(Dispatchers.IO)  {
        try {
            dao.transaction {
                ids.chunked(999).forEach { chunk ->
                    dao.markNewsItemsAsRead(isRead = true, chunk)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun upsertNewsItem(newsItem: NewsItem, forceUpdate: Boolean): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            dao.upsertNewsItem(newsItem.toNewsItemEntity(), forceUpdate)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
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
    ): Result<NewsFeed?, DataError.Remote> = withContext(Dispatchers.IO) {
        Logger.i("Refreshing newsfeed '$feedName', loadArticles=$loadArticles...")
        try {
            val updated = if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                val response = httpClient?.get(urlString = url)
                val xml = response?.bodyAsText()
                val newsFeed = readFromString(feedName, xml)
                val totalItems = newsFeed.items.size
                val totalSteps = totalItems * (if (loadArticles) 2 else 1)
                var currentStep = 0
                val updatedItems = dao.transactionWithResult {
                    dao.upsertNewsFeed(newsFeed.toNewsFeedEntity())
                    val newsItems = newsFeed.items.mapIndexed { index, item ->
                        val newsItemEntity = item.toNewsItemEntity()
                        val item = dao.upsertNewsItem(newsItemEntity)
                        currentStep++
                        progress(currentStep.toFloat() / totalSteps)
                        item
                    }.map { newsItemEntity -> newsItemEntity.toNewsItem() }
                    dao.cleanupOldReadNewsItems(OffsetDateTime.now().minus(Duration.of(keepReadArticlesInDays, ChronoUnit.DAYS)).toInstant().toEpochMilli())
                    dao.cleanupOldUnreadNewsItems(OffsetDateTime.now().minus(Duration.of(keepUnreadArticlesInDays, ChronoUnit.DAYS)).toInstant().toEpochMilli())
                    newsItems
                }

                imageCache.prefetchImages(updatedItems.map { item -> item.image }.filter { url -> url.isNotEmpty() })

                val updatedUpdatedItems = if (loadArticles) {
                    coroutineScope {
                        val semaphore = Semaphore(5)
                        val completedArticles = AtomicInteger(0)
                        updatedItems.mapIndexed { index, newsItem ->
                            async {
                                try {
                                    semaphore.withPermit {
                                        val articleResult = articleRepository.readFullArticle(itemId = newsItem.id, url = newsItem.link)
                                        val item = when (articleResult) {
                                            is Result.Success -> {
                                                newsItem.copy(newsArticle = articleResult.data)
                                            }

                                            is Result.Error -> {
                                                Logger.e("Could not read article: ${newsItem.link}", articleResult.throwable)
                                                newsItem
                                            }
                                        }

                                        val done = completedArticles.incrementAndGet()
                                        progress((totalItems + done).toFloat() / totalSteps)
                                        item
                                    }
                                } catch (e: Exception) {
                                    Logger.e("Could not fetch article", e)
                                    newsItem
                                }
                            }
                        }.awaitAll()
                    }
                } else {
                    updatedItems
                }

                newsFeed.copy(items = updatedUpdatedItems)
            } else {
                dao.getNewsFeedByFeedName(feedName).executeAsOneOrNull()?.toNewsFeed()
            }

            Result.Success(updated)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }

    override suspend fun readFromString(
        feedName: String,
        xml: String?
    ): NewsFeed = withContext(Dispatchers.IO) {
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
