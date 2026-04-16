package de.visualdigits.newshomereader.domain.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HasPart(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val startOffset: Double? = null,
    val endOffset: Double? = null,
    val url: String? = null
)
