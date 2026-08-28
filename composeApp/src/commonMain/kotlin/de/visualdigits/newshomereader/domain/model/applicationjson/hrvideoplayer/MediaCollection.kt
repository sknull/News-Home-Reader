package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaCollection(
    @SerialName("streams") val streams: List<Stream> = listOf(),
    @SerialName("meta") val meta: Meta? = null,
    @SerialName("pluginData") val pluginData: PluginData? = null,
    @SerialName("geoBlocked") val geoBlocked: Boolean? = null
)
