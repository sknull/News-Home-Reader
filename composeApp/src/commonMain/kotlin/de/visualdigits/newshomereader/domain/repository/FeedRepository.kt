package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FeedRepository {

    suspend fun readFromFile(
        feedName: String,
        file: File
    ): NewsFeed?

    suspend fun getFeedItemsByNewsFeedName(
        feedName: String
    ): Result<List<NewsItem>, DataError.Remote>

    fun observeFeedItems(newsFeedGroup: NewsFeedGroup?, newsFeedName: String?): Flow<List<NewsItem>>

    suspend fun upsertNewsFeed(
        newsFeed: NewsFeed
    ): Result<Unit, DataError.Local>

    suspend fun markNewsItemsAsRead(
        ids: List<Long>,
    ): Result<Unit, DataError.Local>

    suspend fun upsertNewsItem(
        newsItem: NewsItem,
        forceUpdate: Boolean = false
    ): Result<Pair<NewsItem, Boolean>, DataError.Local>

    suspend fun refreshNewsFeeds(
        newsFeedItems: List<NewsFeedItem>,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float, ProgressStage) -> Unit,
    ): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote>

    suspend fun refreshNewsFeed(
        feedName: String,
        url: String,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float, ProgressStage) -> Unit,
    ): Result<Pair<NewsFeed?, Boolean>, DataError.Remote>

    suspend fun prefetchImages(
        newsFeeds: List<NewsFeed>,
        progress: (Float, ProgressStage) -> Unit
    ): Result<Unit, DataError.Remote>

    suspend fun readFromBytes(
        feedName: String?,
        bytes: ByteArray?
    ): NewsFeed?
}
