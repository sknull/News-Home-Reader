package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MediaItem(
    val url: String? = null,
    val headline: String? = null,
    val caption: String? = null,
    val description: String? = null,
    val author: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) val datePublished: KmpOffsetDateTime? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) val dateModified: KmpOffsetDateTime? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) val uploadDate: KmpOffsetDateTime? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) val expires: KmpOffsetDateTime? = null,
    val keywords: List<String> = listOf(),
    val thumbnails: List<ThumbnailItem> = listOf(),
    val type: MediaType = MediaType.unknown
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaItem

        if (url != other.url) return false
        if (dateModified != other.dateModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url?.hashCode() ?: 0
        result = 31 * result + (dateModified?.hashCode() ?: 0)
        return result
    }
}
