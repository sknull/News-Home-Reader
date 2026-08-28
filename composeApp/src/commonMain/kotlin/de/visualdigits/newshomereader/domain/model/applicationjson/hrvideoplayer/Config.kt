package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("heartbeatDuration") val heartbeatDuration: HeartbeatDuration? = null,
    @SerialName("events") val events: List<String> = listOf()
)
