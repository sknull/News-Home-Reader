package de.visualdigits.newshomereader.domain.model.applicationjson

import de.visualdigits.newshomereader.data.model.applicationjson.PublisherDto
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.data.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Video(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("duration") val duration: String? = null,
    @Serializable(with = ListSerializer::class) val thumbnailUrl: List<String> = listOf(),
    @Serializable(with = ListSerializer::class) val contentUrl: List<String> = listOf(),
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @SerialName("uploadDate") val uploadDate: OffsetDateTime? = null,
    @SerialName("publisher") val publisher: PublisherDto? = null
)
