package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Audio(
    @SerialName("kind") val kind: String? = null,
    @SerialName("languageCode") val languageCode: String? = null
)
