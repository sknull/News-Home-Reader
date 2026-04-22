package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import java.time.OffsetDateTime

@Immutable
data class NewsItem(
    val id: Long = 0L,
    val feedName: String,
    val identifier: String,
    val published: OffsetDateTime,
    val updated: OffsetDateTime,
    val link: String,
    val title: String,
    val summary: String,
    val content: String,
    val keywords: List<String> = listOf(),
    val image: String,
    val imageTitle: String,
    val imageCaption: String,
    val isRead: Boolean = false,
    val newsFeed: NewsFeed? = null,
    val newsArticle: FullArticle? = null,
    val isChanged: Boolean = false,
): Comparable<NewsItem> {

    override fun compareTo(other: NewsItem): Int = compareBy<NewsItem>(
        { it.published }, { it.updated }
    ).compare(this, other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsItem

        if (feedName != other.feedName) return false
        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feedName.hashCode()
        result = 31 * result + identifier.hashCode()
        return result
    }
}
