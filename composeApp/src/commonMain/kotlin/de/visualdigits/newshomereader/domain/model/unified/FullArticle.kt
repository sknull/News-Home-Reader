package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.domain.model.applicationjson.AppJson

@Immutable
data class FullArticle(
    val id: Long,
    val itemId: Long,
    val applicationJson: List<AppJson> = listOf(),
    val html: String,
    val imageItems: List<MediaItem> = listOf(),
    val videoItems: List<MediaItem> = listOf(),
    val audioItems: List<MediaItem> = listOf(),
    val articleImage: String? = null,
    val discussionUrl: String? = null,
    val commentCount: Long = 0L,
    val isFree: Boolean = true,
    val wordCount: Long = 0L,
    val readingTime: Long = 0L,
    val retries: Long = 0L,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FullArticle) return false

        return itemId == other.itemId &&
                commentCount == other.commentCount &&
                isFree == other.isFree &&
                wordCount == other.wordCount &&
                readingTime == other.readingTime &&
                applicationJson == other.applicationJson &&
                html == other.html &&
                imageItems == other.imageItems &&
                videoItems == other.videoItems &&
                audioItems == other.audioItems &&
                articleImage == other.articleImage &&
                discussionUrl == other.discussionUrl
    }

    override fun hashCode(): Int {
        var result = commentCount.hashCode()
        result = 31 * result + isFree.hashCode()
        result = 31 * result + wordCount.hashCode()
        result = 31 * result + readingTime.hashCode()
        result = 31 * result + applicationJson.hashCode()
        result = 31 * result + html.hashCode()
        result = 31 * result + imageItems.hashCode()
        result = 31 * result + videoItems.hashCode()
        result = 31 * result + audioItems.hashCode()
        result = 31 * result + (articleImage?.hashCode() ?: 0)
        result = 31 * result + (discussionUrl?.hashCode() ?: 0)
        return result
    }
}
