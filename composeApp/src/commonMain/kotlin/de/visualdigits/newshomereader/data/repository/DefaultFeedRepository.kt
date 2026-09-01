package de.visualdigits.newshomereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.parser.Parser
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.errorhandling.LogMessage.Companion.log
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.toNewsFeed
import de.visualdigits.newshomereader.data.database.toNewsFeedEntity
import de.visualdigits.newshomereader.data.database.toNewsItem
import de.visualdigits.newshomereader.data.database.toNewsItemEntity
import de.visualdigits.newshomereader.data.database.upsertNewsFeed
import de.visualdigits.newshomereader.data.database.upsertNewsItem
import de.visualdigits.newshomereader.data.mapper.toNewsFeed
import de.visualdigits.newshomereader.data.model.atom.Feed
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.data.model.rss.Rss
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import de.visualdigits.newshomereader.domain.webdav.WebDavSyncService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalAtomicApi::class)
class DefaultFeedRepository(
    private val httpClient: HttpClient,
    private val dao: NewsHomeReaderDatabaseQueries,
    private val imageCache: ImageCache,
    private val articleRepository: ArticleRepository,
    private val webDavSyncService: WebDavSyncService,
    private val scope: CoroutineScope,
) : FeedRepository {

    override suspend fun getAllNewsFeeds(): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        try {
            Result.Success(Pair(dao.getAllNewsFeeds().executeAsList().map { nf -> nf.toNewsFeed() }, false))
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedByFeedName(feedName: String): Result<Pair<NewsFeed?, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        try {
            Result.Success(Pair(dao.getNewsFeedByFeedName(feedName).executeAsOneOrNull()?.toNewsFeed(), false))
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }

    override suspend fun getAllFeedItems(): Result<List<NewsItem>, DataError.Remote> = withContext(Dispatchers.IO) {
        try {
            val data = dao.getAllNewsItemsWithArticles()
                .executeAsList()
                .map { composit -> composit.toNewsItem() }
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNewsFeedItemSearchItems(query: String): Flow<List<NewsItem>> {
        return dao.searchNewsItems(query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .flatMapLatest { searchNewsItems ->
                if (searchNewsItems.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val itemFlows = searchNewsItems.map { sni ->
                        val ni = sni.toNewsItem()
                        dao.getNewsFeedByFeedName(sni.feedName)
                            .asFlow()
                            .mapToOneOrNull(Dispatchers.IO)
                            .map { newsFeedEntity ->
                                ni.copy(newsFeed = newsFeedEntity?.toNewsFeed())
                            }
                    }

                    combine(itemFlows) { itemsArray ->
                        itemsArray.toList()
                    }
                }
            }
    }

    override fun observeFeedItems(newsFeedGroup: NewsFeedGroup?, newsFeedName: String?): Flow<List<NewsItem>> {
        val allFlows = buildList {
            newsFeedGroup?.let { newsFeedGroup ->
                dao.getNewsFeedGroupEntityByName(newsFeedGroup.name, newsFeedGroup.parentGroupName)
                    .executeAsOneOrNull()
                    ?.let { newsFeedGroupEntity ->
                        val names = dao.getNewsFeedGroupEntitiesByParentName(newsFeedGroupEntity.name)
                            .executeAsList()
                            .flatMap { subGroupEntity ->
                                subGroupEntity.newsFeeds.map { nf -> nf.name }
                            } + newsFeedGroupEntity.newsFeeds.map { it.name }
                        addAll(names.map { observeNewsItems(it) })
                    }
            }
            newsFeedName?.let { add(observeNewsItems(it)) }
        }

        return when {
            allFlows.isNotEmpty() -> combine(allFlows) { it.flatMap { list -> list } }
            else -> flowOf(emptyList())
        }
    }

    private fun observeNewsItems(newsFeedName: String?): Flow<List<NewsItem>> {
        val newsItems = if (newsFeedName != null) {
            val newsFeed = dao.getNewsFeedByFeedName(newsFeedName).executeAsOneOrNull()?.toNewsFeed()
            dao.getAllNewsItemsByFeedName(newsFeedName.trim().lowercase())
                .asFlow()
                .mapToList(Dispatchers.IO)
                .onStart { emit(emptyList()) }
                .map { newsItemEntities ->
                    newsItemEntities.map { newsItemEntity ->
                        newsItemEntity.toNewsItem().copy(newsFeed = newsFeed)
                    }
                }
        } else emptyFlow()
        return newsItems
    }

    override suspend fun upsertNewsFeed(newsFeed: NewsFeed): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            dao.upsertNewsFeed(newsFeed.toNewsFeedEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Something went wrong during upserting news feed", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun markNewsItemsAsRead(newsItems: List<NewsItem>): Result<Unit, DataError.Local> = withContext(Dispatchers.IO)  {
        try {
            dao.transaction {
                newsItems
                    .map { newsItem -> newsItem.id }
                    .chunked(999).forEach { chunk ->
                    dao.markNewsItemsAsReadById(isRead = true, chunk)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Something went wrong during marking news item as read", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun synchroniseReadNewsItems(): Result<Unit, DataError.Local> = withContext(Dispatchers.IO)  {
        try {
            val localReadNewsItems = dao.getReadNewsItems().executeAsList()
            val syncResult = webDavSyncService.syncReadStatus(localReadNewsItems
                .map { newsItem -> "${newsItem.feedName}||||${newsItem.identifier}" }.toSet())
            if (syncResult is Result.Success) {
                val identifiers = syncResult.data
                    .mapNotNull { id ->
                        val parts = id.split("||||")
                        if (parts.size == 2) {
                            Pair(parts[0], parts[1])
                        } else {
                            null
                        }
                    }
                if (identifiers.isNotEmpty()) {
                    dao.transaction {
                        identifiers.forEach { identifier -> dao.markNewsItemsAsReadByFeedNameAndIdentifier(
                            feedName = identifier.first,
                            identifier = identifier.second
                        ) }
                    }
                }
            } else if (syncResult is Result.Error) {
                Logger.e("Something went wrong while synchronizing read news items with webdav host", syncResult.throwable)
                Result.Error(DataError.Local.UNKNOWN)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Something went wrong while synchronizing read news items with webdav host", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun upsertNewsItem(newsItem: NewsItem, forceUpdate: Boolean): Result<Pair<NewsItem, Boolean>, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val (insertedNewsItemEntity, changed) = dao.upsertNewsItem(newsItem.toNewsItemEntity(), forceUpdate)
            Result.Success(Pair(insertedNewsItemEntity.toNewsItem(), changed))
        } catch (e: Exception) {
            Logger.e("Something went wrong during upserting news item", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    private val refreshMutex = Mutex()
    private var activeRefreshDeferred: Deferred<Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote>>? = null

    override suspend fun refreshNewsFeeds(
        newsFeedItems: List<NewsFeedItem>,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        loadArticles: Boolean
    ): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        val deferredToAwait = refreshMutex.withLock {
            activeRefreshDeferred ?: coroutineScope {
                scope.async(Dispatchers.IO) {
                    try {
                        executeRefreshNewsFeeds(
                            newsFeedItems = newsFeedItems,
                            keepReadArticlesInDays = keepReadArticlesInDays,
                            keepUnreadArticlesInDays = keepUnreadArticlesInDays,
                            loadArticles = loadArticles
                        )
                    } finally {
                        refreshMutex.withLock {
                            activeRefreshDeferred = null
                        }
                    }
                }.also {
                    activeRefreshDeferred = it
                }
            }
        }

        deferredToAwait.await()
    }

    private suspend fun executeRefreshNewsFeeds(
        newsFeedItems: List<NewsFeedItem>,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        loadArticles: Boolean
    ): Result.Success<Pair<List<NewsFeed>, Boolean>> {
        val newsFeeds = newsFeedItems.mapNotNull { newsFeedItem ->
            log(Severity.Info, "Refreshing newsfeed '${newsFeedItem.name}'", withTag = "NHR")
            try {
                withContext(Dispatchers.IO + NonCancellable) {
                    val response = newsFeedItem.url?.let { u -> httpClient.get(urlString = u) }
                    readFromBytes(newsFeedItem.name, response?.readRawBytes())
                }
            } catch (e: Exception) {
                Logger.e("DefaultFeedRepository: Could not refresh feed '${newsFeedItem.name}'", e)
                null
            }
        }
        val result = refreshNewsFeedItems(
            newsFeeds,
            keepReadArticlesInDays = keepReadArticlesInDays,
            keepUnreadArticlesInDays = keepUnreadArticlesInDays,
            loadArticles = loadArticles
        )
        return Result.Success(result)
    }

    private suspend fun refreshNewsFeedItems(
        newsFeeds: List<NewsFeed>,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        loadArticles: Boolean
    ): Pair<List<NewsFeed>, Boolean> = withContext(Dispatchers.IO) {
        try {
            val (persistedItems, _) = dao.transactionWithResult {
                val persistedNewsFeeds = newsFeeds.map { newsFeed ->
                    persistNewsFeed(newsFeed)
                }
                val result = if (persistedNewsFeeds.isNotEmpty()) {
                    persistedNewsFeeds.reduce { acc, pair ->
                        Pair(acc.first + pair.first, acc.second || pair.second)
                    }
                } else {
                    Pair(listOf(), false)
                }
                dao.cleanupOldReadNewsItems(
                    KmpOffsetDateTime.now().minus(keepReadArticlesInDays.days).toInstant().toEpochMilliseconds()
                )
                dao.cleanupOldUnreadNewsItems(
                    KmpOffsetDateTime.now().minus(keepUnreadArticlesInDays.days).toInstant().toEpochMilliseconds()
                )
                result
            }

            val newsFeedsMap = newsFeeds
                .associate { nf -> Pair(nf.feedName, nf.copy(items = listOf())) }
                .toMutableMap()

            val (newsItemsWithArticles, changedArticles) = if (loadArticles) {
                loadArticles(persistedItems)
            } else {
                Pair(persistedItems, false)
            }
            val newsFeedsWithArticles = newsItemsWithArticles.mapNotNull { item ->
                item.newsFeed?.let { nf ->
                    val newsFeed = newsFeedsMap[nf.feedName]
                    newsFeed
                        ?.copy(items = newsFeed.items + item)
                        ?.also { nf -> newsFeedsMap[nf.feedName] = nf }
                }
            }

            Logger.i("Refresh finished")
            Pair(newsFeedsWithArticles, changedArticles)
        } catch (e: Exception) {
            Logger.e("Could not refresh news items", e)
            Pair(newsFeeds, false)
        }
    }

    override suspend fun refreshNewsFeed(
        feedName: String,
        url: String,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean
    ): Result<Pair<NewsFeed?, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        Logger.i("Refreshing newsfeed '$feedName', loadArticles=$loadArticles...")
        try {
            val newsFeed = withContext(Dispatchers.IO + NonCancellable) {
                val response = httpClient.get(urlString = url)
                readFromBytes(feedName, response.readRawBytes())
            }

            val (persistedItems, changedNewsItems) = dao.transactionWithResult {
                val newsItems = persistNewsFeed(newsFeed)
                dao.cleanupOldReadNewsItems(KmpOffsetDateTime.now().minus(keepReadArticlesInDays.days).toInstant().toEpochMilliseconds())
                dao.cleanupOldUnreadNewsItems(KmpOffsetDateTime.now().minus(keepUnreadArticlesInDays.days).toInstant().toEpochMilliseconds())
                newsItems
            }

            val (articles, changedArticles) = if (loadArticles) {
                loadArticles(persistedItems)
            } else {
                Pair(persistedItems, false)
            }
            val newsFeedWithArticles = newsFeed?.copy(items = articles)

            val updatedNewsFeed = Pair(newsFeedWithArticles, changedNewsItems || changedArticles)

            Result.Success(updatedNewsFeed)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }

    override suspend fun prefetchImages(
        newsFeeds: List<NewsFeed>
    ): Result<Unit, DataError.Remote> = withContext(Dispatchers.IO) {
        val feedUrls = newsFeeds
            .map { feed -> feed.image }
            .filter { url -> url.isNotEmpty() }
        val itemUrls = newsFeeds
            .flatMap { feed ->
                feed.items.map { item -> item.image }
            }
        val articleUrls = newsFeeds
            .flatMap { feed ->
                feed.items
                    .filter { item -> item.newsArticle != null }
                    .flatMap { item ->
                        val newsArticle = item.newsArticle!!
                        listOfNotNull(newsArticle.articleImage) +
                        newsArticle.audioItems.flatMap { ai -> ai.thumbnails.flatMap { t -> t.url } } +
                        newsArticle.videoItems.flatMap { ai -> ai.thumbnails.flatMap { t -> t.url } }
                    }
            }
        val urls = (feedUrls + itemUrls + articleUrls).distinct()
        if (urls.isNotEmpty()) {
            Logger.i("Prefetching ${feedUrls.size} feed images, ${itemUrls.size} item images, ${articleUrls.size} article images = ${urls.size} distinct images")
            imageCache.prefetchImages(urls)
        }
        Logger.i("Finished prefetching")

        Result.Success(Unit)
    }

    private fun persistNewsFeed(
        newsFeed: NewsFeed?
    ): Pair<List<NewsItem>, Boolean> {
        return if (newsFeed != null) {
            val changedFeed = dao.upsertNewsFeed(newsFeed.toNewsFeedEntity())
            var changedItems = false
            val persistedNewsItems = newsFeed.items.map { newsItem ->
                val (insertedItem, changedItem) = dao.upsertNewsItem(newsItem.toNewsItemEntity())
                changedItems = changedItems || changedItem
                insertedItem.toNewsItem().copy(newsFeed = newsFeed, isChanged = changedItem)
            }
            Pair(persistedNewsItems, changedFeed || changedItems)
        } else {
            Pair(listOf(), false)
        }
    }

    private suspend fun loadArticles(
        newsItems: List<NewsItem>,
    ): Pair<List<NewsItem>, Boolean> {
        var changed = false
        log(Severity.Info, "Loading articles", withTag = "NHR")
        val newsItemsWithArticles = coroutineScope {
            val semaphore = Semaphore(5)
            newsItems.map { newsItem ->
                async {
                    try {
                        semaphore.withPermit {
                            val articleResult = articleRepository.readFullArticle(newsItem = newsItem)
                            val item = when (articleResult) {
                                is Result.Success -> {
                                    changed = changed || articleResult.data.second
                                    newsItem.copy(newsArticle = articleResult.data.first)
                                }

                                is Result.Error -> {
                                    log(Severity.Error, "Could not read article [${newsItem.id}]: ${newsItem.link}", articleResult.throwable, withTag = "NHR")
                                    newsItem
                                }
                            }

                            item
                        }
                    } catch (e: Exception) {
                        log(Severity.Error, "Could not fetch article for newsItem [${newsItem.id}] ${newsItem.newsFeed?.feedName}/${newsItem.identifier}", e, withTag = "NHR")
                        newsItem
                    }
                }
            }.awaitAll()
        }
        log(Severity.Info, "Finished loading articles", withTag = "NHR")

        return Pair(newsItemsWithArticles, changed)
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
            else -> {
                null // Unsupported feed type
            }
        }
    }

    override suspend fun deleteAllNewsItems(): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val articleCount = dao.getArticleCount().executeAsOne()
            val newsItemCount = dao.getNewsItemCount().executeAsOne()
            Logger.i("Clearing $articleCount articles and $newsItemCount newitems")
            dao.transaction {
                dao.deleteAllFullArticles()
                dao.deleteAllNewsItems()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Could not clear news items", e)
            Result.Error(DataError.Local.SERIALIZATION)
        }
    }
}
