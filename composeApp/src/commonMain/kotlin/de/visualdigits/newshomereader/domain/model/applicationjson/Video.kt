package de.visualdigits.newshomereader.domain.model.applicationjson

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.newshomereader.domain.serializer.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val thumbnailUrl: List<String> = listOf(),
    val contentUrl: List<String> = listOf(),
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @SerialName("uploadDate") val uploadDate: KmpOffsetDateTime? = null,
    val publisher: Publisher? = null
)
