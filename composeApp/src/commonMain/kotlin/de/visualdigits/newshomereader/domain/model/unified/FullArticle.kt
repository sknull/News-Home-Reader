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
    val readingTime: Long = 0L
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FullArticle

        return itemId == other.itemId
    }

    override fun hashCode(): Int {
        return itemId.hashCode()
    }
}
