package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeartbeatDuration(
    @SerialName("0") val x0: Int? = null,
    @SerialName("1") val x1: Int? = null,
    @SerialName("5") val x5: Int? = null
)
