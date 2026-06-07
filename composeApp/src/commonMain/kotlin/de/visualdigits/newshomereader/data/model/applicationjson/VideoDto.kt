package de.visualdigits.newshomereader.data.model.applicationjson


import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.ThumbnailItem
import de.visualdigits.newshomereader.domain.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class VideoDto(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("duration") val duration: String? = null,
    @Serializable(with = ListSerializer::class) val thumbnailUrl: List<String> = listOf(),
    @Serializable(with = ListSerializer::class) val contentUrl: List<String> = listOf(),
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @SerialName("uploadDate") val uploadDate: OffsetDateTime? = null,
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

