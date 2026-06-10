package de.visualdigits.newshomereader.data.model.youtube


import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.ThumbnailItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OEmbed(
    @SerialName("title") val title: String? = null,
    @SerialName("author_name") val authorName: String? = null,
    @SerialName("author_url") val authorUrl: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("version") val version: String? = null,
    @SerialName("provider_name") val providerName: String? = null,
    @SerialName("provider_url") val providerUrl: String? = null,
    @SerialName("thumbnail_height") val thumbnailHeight: Int? = null,
    @SerialName("thumbnail_width") val thumbnailWidth: Int? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("html") val html: String? = null
) {

    fun toMediaItem(videoUrl: String): MediaItem {
        return MediaItem(
            url = videoUrl,
            headline = title,
            description = authorName,
            thumbnails = thumbnailUrl?.let { tu -> listOf(ThumbnailItem(
                url = listOf(tu),
                author = authorName,
            )) } ?: listOf()
        )
    }
}
