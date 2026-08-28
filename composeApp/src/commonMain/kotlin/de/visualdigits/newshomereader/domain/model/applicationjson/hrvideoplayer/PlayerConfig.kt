package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerConfig(
    @SerialName("generic") val generic: Generic? = null,
    @SerialName("web") val web: Web? = null,
    @SerialName("pluginData") val pluginData: PluginData? = null
)
