package de.visualdigits.newshomereader.domain.model.applicationjson

import de.visualdigits.newshomereader.data.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Video(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val thumbnailUrl: List<String> = listOf(),
    val contentUrl: List<String> = listOf(),
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @SerialName("uploadDate") val uploadDate: OffsetDateTime? = null,
    val publisher: Publisher? = null
)
