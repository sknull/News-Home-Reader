package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Size(
    @SerialName("minWidth") val minWidth: Int? = null,
    @SerialName("value") val value: String? = null
)
