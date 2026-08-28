package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    suspend fun getAllNewsFeeds(): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote>

    suspend fun getNewsFeedByFeedName(feedName: String): Result<Pair<NewsFeed?, Boolean>, DataError.Remote>

    suspend fun getAllFeedItems(): Result<List<NewsItem>, DataError.Remote>

    suspend fun getFeedItemsByNewsFeedName(
        feedName: String
    ): Result<List<NewsItem>, DataError.Remote>

    fun observeFeedItems(newsFeedGroup: NewsFeedGroup?, newsFeedName: String?): Flow<List<NewsItem>>

    fun observeNewsFeedItemSearchItems(query: String): Flow<List<NewsItem>>

    suspend fun upsertNewsFeed(
        newsFeed: NewsFeed
    ): Result<Unit, DataError.Local>

    suspend fun markNewsItemsAsRead(
        newsItems: List<NewsItem>,
    ): Result<Unit, DataError.Local>

    suspend fun synchroniseReadNewsItems(): Result<Unit, DataError.Local>

    suspend fun upsertNewsItem(
        newsItem: NewsItem,
        forceUpdate: Boolean = false
    ): Result<Pair<NewsItem, Boolean>, DataError.Local>

    suspend fun refreshNewsFeeds(
        newsFeedItems: List<NewsFeedItem>,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        loadArticles: Boolean
    ): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote>

    suspend fun refreshNewsFeed(
        feedName: String,
        url: String,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean
    ): Result<Pair<NewsFeed?, Boolean>, DataError.Remote>

    suspend fun prefetchImages(
        newsFeeds: List<NewsFeed>
    ): Result<Unit, DataError.Remote>

    suspend fun readFromBytes(
        feedName: String?,
        bytes: ByteArray?
    ): NewsFeed?

    suspend fun deleteAllNewsItems(): Result<Unit, DataError.Local>
}
