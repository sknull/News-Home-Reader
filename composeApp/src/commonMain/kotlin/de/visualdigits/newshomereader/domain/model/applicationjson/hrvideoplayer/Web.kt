package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Web(
    @SerialName("baseUrl") val baseUrl: String? = null,
    @SerialName("isForcedAutoplay") val isForcedAutoplay: Boolean? = null,
    @SerialName("isForcedVideoView") val isForcedVideoView: Boolean? = null
)
