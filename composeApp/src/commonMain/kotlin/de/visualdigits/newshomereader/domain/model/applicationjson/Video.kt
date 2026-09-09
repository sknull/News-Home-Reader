package de.visualdigits.newshomereader.domain.model.applicationjson

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("duration") val duration: String? = null,
    @SerialName("thumbnailUrl") val thumbnailUrl: List<String> = listOf(),
    @SerialName("contentUrl") val contentUrl: List<String> = listOf(),
    @SerialName("uploadDate") val uploadDate: KmpOffsetDateTime? = null,
    @SerialName("publisher") val publisher: Publisher? = null
)
