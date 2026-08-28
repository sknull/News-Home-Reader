package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime

@Immutable
data class NewsItem(
    val id: Long = 0L,
    val feedName: String,
    val identifier: String,
    val published: KmpOffsetDateTime?,
    val updated: KmpOffsetDateTime?,
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

    val uiKey: String
        get() = "${link.takeIf { it.isNotBlank() } ?: title}_$published"

    override fun compareTo(other: NewsItem): Int = compareBy<NewsItem>(
        { it.published }, { it.updated }
    ).compare(this, other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewsItem) return false

        return isRead == other.isRead &&
                isChanged == other.isChanged &&
                feedName == other.feedName &&
                identifier == other.identifier &&
                published == other.published &&
                updated == other.updated &&
                link == other.link &&
                title == other.title &&
                summary == other.summary &&
                content == other.content &&
                keywords == other.keywords &&
                image == other.image &&
                imageTitle == other.imageTitle &&
                imageCaption == other.imageCaption &&
                newsFeed == other.newsFeed &&
                newsArticle == other.newsArticle
    }

    override fun hashCode(): Int {
        var result = isRead.hashCode()
        result = 31 * result + isChanged.hashCode()
        result = 31 * result + feedName.hashCode()
        result = 31 * result + identifier.hashCode()
        result = 31 * result + published.hashCode()
        result = 31 * result + updated.hashCode()
        result = 31 * result + link.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + summary.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + keywords.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + imageTitle.hashCode()
        result = 31 * result + imageCaption.hashCode()
        result = 31 * result + (newsFeed?.hashCode() ?: 0)
        result = 31 * result + (newsArticle?.hashCode() ?: 0)
        return result
    }
}
