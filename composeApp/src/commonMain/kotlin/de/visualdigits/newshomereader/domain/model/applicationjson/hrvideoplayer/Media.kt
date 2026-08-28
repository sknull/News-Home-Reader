package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    @SerialName("url") val url: String? = null,
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("audios") val audios: List<Audio> = listOf()
)
