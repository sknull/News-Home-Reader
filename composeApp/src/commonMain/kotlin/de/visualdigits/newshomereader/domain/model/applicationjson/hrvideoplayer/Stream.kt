package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stream(
    @SerialName("kind") val kind: String? = null,
    @SerialName("isAudioOnly") val isAudioOnly: Boolean? = null,
    @SerialName("media") val media: List<Media> = listOf()
)
