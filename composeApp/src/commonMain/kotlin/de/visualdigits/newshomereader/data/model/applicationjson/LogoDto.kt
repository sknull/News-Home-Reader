package de.visualdigits.newshomereader.data.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val url: String? = null,
    val caption: String? = null,
    val contentUrl: String? = null,
    val inLanguage: String? = null,
    val width: Int? = null,
    val height: Int? = null
)
