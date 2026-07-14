package de.visualdigits.newshomereader.data.model.applicationjson


import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.KmpOffsetDateTimeHeuristicDeserializer
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.ThumbnailItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("duration") val duration: String? = null,
    @Serializable(with = ListSerializer::class) val thumbnailUrl: List<String> = listOf(),
    @Serializable(with = ListSerializer::class) val contentUrl: List<String> = listOf(),
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @SerialName("uploadDate") val uploadDate: KmpOffsetDateTime? = null,
    @SerialName("publisher") val publisher: PublisherDto? = null
) {

    fun toMediaItem(): MediaItem {
        return MediaItem(
            url = contentUrl.firstOrNull(),
            duration = duration,
            description = description,
            datePublished = uploadDate,
            dateModified = uploadDate,
            uploadDate = uploadDate,
            thumbnails = thumbnailUrl.map { url ->
                ThumbnailItem(
                    url = listOf(url),
                )
            }
        )
    }
}

