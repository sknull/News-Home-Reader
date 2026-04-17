package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FeedRepository {

    suspend fun readFromFile(
        feedName: String,
        file: File
    ): NewsFeed

    suspend fun getFeedItemsByNewsFeedName(
        feedName: String
    ): Result<List<NewsItem>, DataError.Remote>

    fun observeFeedItems(feedName: String): Flow<List<NewsItem>>

    suspend fun upsertNewsFeed(
        newsFeed: NewsFeed
    ): Result<Unit, DataError.Local>

    suspend fun markNewsItemsAsRead(
        ids: List<Long>,
    ): Result<Unit, DataError.Local>

    suspend fun upsertNewsItem(
        newsItem: NewsItem,
        forceUpdate: Boolean = false
    ): Result<Unit, DataError.Local>

    suspend fun refreshNewsFeed(
        feedName: String,
        url: String,
        wifiOnly: Boolean,
        keepReadArticlesInDays: Long,
        keepUnreadArticlesInDays: Long,
        maxImageSize: Int,
        loadArticles: Boolean,
        progress: (Float) -> Unit,
    ): Result<NewsFeed?, DataError.Remote>

    suspend fun readFromString(
        feedName: String,
        xml: String?
    ): NewsFeed
}
