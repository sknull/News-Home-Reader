package de.visualdigits.newshomereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.parser.Parser
import de.visualdigits.common.domain.model.errorhandling.LogMessage.Companion.log
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.presentation.components.ConnectivityManager
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
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Duration
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger

class DefaultFeedRepository(
    private val httpClient: HttpClient,
    private val dao: NewsHomeReaderDatabaseQueries,
    private val connectivityManager: ConnectivityManager,
    private val imageCache: ImageCache,
    private val articleRepository: ArticleRepository,
    private val webDavSyncService: WebDavSyncService
) : FeedRepository {

    private val log = Logger.withTag("DefaultFeedRepository")

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
            log(Severity.Error, "Something went wrong during upserting news feed", e, withTag = "NHR")
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
            log(Severity.Error, "Something went wrong during marking news item as read", e, withTag = "NHR")
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
                log(Severity.Error, "Something went wrong while synchronizing read news items with webdav host", syncResult.throwable, withTag = "NHR")
                Result.Error(DataError.Local.UNKNOWN)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            log(Severity.Error, "Something went wrong while synchronizing read news items with webdav host", e, withTag = "NHR")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun upsertNewsItem(newsItem: NewsItem, forceUpdate: Boolean): Result<Pair<NewsItem, Boolean>, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val (insertedNewsItemEntity, changed) = dao.upsertNewsItem(newsItem.toNewsItemEntity(), forceUpdate)
            Result.Success(Pair(insertedNewsItemEntity.toNewsItem(), changed))
        } catch (e: Exception) {
            log(Severity.Error, "Something went wrong during upserting news item", e, withTag = "NHR")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    private val currentStep = AtomicInteger(0)

    override suspend fun refreshNewsFeeds(
        newsFeedItems: List<NewsFeedItem>,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float, ProgressStage) -> Unit
    ): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        try {

            val finalNewsFeeds = if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                val totalSteps1 = newsFeedItems.size
                currentStep.set(0)
                val newsFeeds = newsFeedItems.mapNotNull { newsFeedItem ->
                    log(Severity.Info, "Refreshing newsfeed '${newsFeedItem.name}', loadArticles=$loadArticles...", withTag = "NHR")
                    try {
                        val response = newsFeedItem.url?.let { u -> httpClient.get(urlString = u) }
                        val newsFeed = readFromBytes(newsFeedItem.name, response?.readRawBytes())
                        val done = currentStep.incrementAndGet()
                        progress(done.toFloat() / totalSteps1, ProgressStage.LOAD_FEEDS)
                        newsFeed
                    } catch (e: Exception) {
                        log(Severity.Error, "Could not load feed '${newsFeedItem.name}'", e, withTag = "NHR")
                        null
                    }
                }

                val newsItems = newsFeeds.flatMap { newsFeed -> newsFeed.items }
                val totalSteps2 = newsFeeds.size + newsItems.size * (if (loadArticles) 2 else 1)
                currentStep.set(0)

                val (persistedItems, _) = dao.transactionWithResult {
                    val persistedNewsFeeds = newsFeeds.map { newsFeed ->
                        persistNewsFeed(newsFeed, totalSteps2, progress)
                    }
                    val result = if (persistedNewsFeeds.isNotEmpty()) {
                        persistedNewsFeeds.reduce { acc, pair ->
                            Pair(acc.first + pair.first, acc.second || pair.second)
                        }
                    } else {
                        Pair(listOf(), false)
                    }
                    dao.cleanupOldReadNewsItems(OffsetDateTime.now().minus(Duration.of(keepReadArticlesInDays, ChronoUnit.DAYS)).toInstant().toEpochMilli())
                    dao.cleanupOldUnreadNewsItems(OffsetDateTime.now().minus(Duration.of(keepUnreadArticlesInDays, ChronoUnit.DAYS)).toInstant().toEpochMilli())
                    result
                }

                val newsFeedsMap = newsFeeds
                    .associate { nf -> Pair(nf.feedName, nf.copy(items = listOf())) }
                    .toMutableMap()

                val (newsItemsWithArticles, changedArticles) = loadArticles(loadArticles, persistedItems, totalSteps2, progress)
                val newsFeedsWithArticles = newsItemsWithArticles.mapNotNull { item ->
                    item.newsFeed?.let { nf ->
                        val newsFeed = newsFeedsMap[nf.feedName]
                        newsFeed
                            ?.copy(items = newsFeed.items + item)
                            ?.also { nf -> newsFeedsMap[nf.feedName] = nf }
                    }
                }

                Pair(newsFeedsWithArticles, changedArticles)
            } else {
                log(Severity.Info, "No free of charge internet connection available - fetching newsFeeds from database", withTag = "NHR")
                val newsFeeds = dao.getAllNewsFeeds().executeAsList().map { nf -> nf.toNewsFeed() }
                Pair(newsFeeds, false)
            }
            log(Severity.Info, "Refresh finished", withTag = "NHR")
            Result.Success(finalNewsFeeds)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
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
        progress: (Float, ProgressStage) -> Unit
    ): Result<Pair<NewsFeed?, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        log(Severity.Info, "Refreshing newsfeed '$feedName', loadArticles=$loadArticles...", withTag = "NHR")
        try {
            val updatedNewsFeed = if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                val response = httpClient.get(urlString = url)
                val newsFeed = readFromBytes(feedName, response.readRawBytes())

                val totalItems = (newsFeed?.items?.size?:0) + 1
                val totalSteps = totalItems * (if (loadArticles) 2 else 1)
                currentStep.set(0)

                val (persistedItems, changedNewsItems) = dao.transactionWithResult {
                    val newsItems = persistNewsFeed(newsFeed, totalSteps, progress)
                    dao.cleanupOldReadNewsItems(OffsetDateTime.now().minus(Duration.of(keepReadArticlesInDays, ChronoUnit.DAYS)).toInstant().toEpochMilli())
                    dao.cleanupOldUnreadNewsItems(OffsetDateTime.now().minus(Duration.of(keepUnreadArticlesInDays, ChronoUnit.DAYS)).toInstant().toEpochMilli())
                    newsItems
                }

                val (articles, changedArticles) = loadArticles(loadArticles, persistedItems, totalSteps, progress)
                val newsFeedWithArticles = newsFeed?.copy(items = articles)

                Pair(newsFeedWithArticles, changedNewsItems || changedArticles)
            } else {
                Pair(dao.getNewsFeedByFeedName(feedName).executeAsOneOrNull()?.toNewsFeed(), false)
            }

            Result.Success(updatedNewsFeed)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }

    override suspend fun prefetchImages(
        newsFeeds: List<NewsFeed>,
        progress: (Float, ProgressStage) -> Unit
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
        val totalSteps = urls.size
        if (totalSteps > 0) {
            currentStep.set(0)
            log(Severity.Info, "Prefetching ${feedUrls.size} feed images, ${itemUrls.size} item images, ${articleUrls.size} article images = ${urls.size} distinct images", withTag = "NHR")
            imageCache.prefetchImages(urls) {
                val done = currentStep.incrementAndGet()
                progress(done.toFloat() / totalSteps, ProgressStage.LOAD_IMAGES)
            }
        }
        log(Severity.Info, "Finished prefetching", withTag = "NHR")

        Result.Success(Unit)
    }

    private fun persistNewsFeed(
        newsFeed: NewsFeed?,
        totalSteps: Int,
        progress: (Float, ProgressStage) -> Unit
    ): Pair<List<NewsItem>, Boolean> {
        return if (newsFeed != null) {
            val changedFeed = dao.upsertNewsFeed(newsFeed.toNewsFeedEntity())
            var changedItems = false
            val done = currentStep.incrementAndGet()
            progress(done.toFloat() / totalSteps, ProgressStage.LOAD_ARTICLES)
            val persistedNewsItems = newsFeed.items.map { newsItem ->
                val (insertedItem, changedItem) = dao.upsertNewsItem(newsItem.toNewsItemEntity())
                changedItems = changedItems || changedItem
                val done = currentStep.incrementAndGet()
                progress(done.toFloat() / totalSteps, ProgressStage.LOAD_ARTICLES)
                insertedItem.toNewsItem().copy(newsFeed = newsFeed, isChanged = changedItem)
            }
            Pair(persistedNewsItems, changedFeed || changedItems)
        } else {
            Pair(listOf(), false)
        }
    }

    private suspend fun loadArticles(
        loadArticles: Boolean,
        newsItems: List<NewsItem>,
        totalSteps: Int,
        progress: (Float, ProgressStage) -> Unit
    ): Pair<List<NewsItem>, Boolean> {
        log(Severity.Info, "Loading articles", withTag = "NHR")
        var changed = false
        val newsItemsWithArticles =  if (loadArticles) {
            coroutineScope {
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

                                val done = currentStep.incrementAndGet()
                                progress(done.toFloat() / totalSteps, ProgressStage.LOAD_ARTICLES)
                                item
                            }
                        } catch (e: Exception) {
                            log(Severity.Error, "Could not fetch article for newsItem [${newsItem.id}] ${newsItem.newsFeed?.feedName}/${newsItem.identifier}",e, withTag = "NHR")
                            newsItem
                        }
                    }
                }.awaitAll()
            }
        } else {
            newsItems
        }

        return Pair(newsItemsWithArticles, changed)
    }

    override suspend fun readFromFile(
        feedName: String,
        file: File
    ): NewsFeed? = withContext(Dispatchers.IO) {
        readFromBytes(feedName, file.readBytes())
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
}
