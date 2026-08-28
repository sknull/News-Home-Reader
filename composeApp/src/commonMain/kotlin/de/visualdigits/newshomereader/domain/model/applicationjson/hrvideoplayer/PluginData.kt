package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginData(
    @SerialName("trackingPiano@all") val trackingPianoall: TrackingPianoall? = null
)
