package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Logo(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("caption") val caption: String? = null,
    @SerialName("contentUrl") val contentUrl: String? = null,
    @SerialName("inLanguage") val inLanguage: String? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null
)
