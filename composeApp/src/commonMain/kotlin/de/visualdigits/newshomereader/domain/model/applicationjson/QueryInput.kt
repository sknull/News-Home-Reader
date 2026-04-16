package de.visualdigits.newshomereader.domain.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class QueryInput(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val valueRequired: Boolean? = null,
    val valueName: String? = null,
    val inLanguage: String? = null,
    val url: String? = null,
    val contentUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val caption: String? = null
)
