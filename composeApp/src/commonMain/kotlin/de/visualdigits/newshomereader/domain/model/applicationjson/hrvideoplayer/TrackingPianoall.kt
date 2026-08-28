package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackingPianoall(
    @SerialName("isEnabled") val isEnabled: Boolean? = null,
    @SerialName("config") val config: Config? = null,
    @SerialName("avContent") val avContent: AvContent? = null
)
