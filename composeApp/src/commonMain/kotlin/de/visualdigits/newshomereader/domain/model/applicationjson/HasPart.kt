package de.visualdigits.newshomereader.domain.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class HasPart(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("startOffset") val startOffset: Double? = null,
    @SerialName("endOffset") val endOffset: Double? = null,
    @SerialName("url") val url: String? = null
)
