package de.visualdigits.newshomereader.domain.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class Publisher(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("alternateName") val alternateName: List<String> = listOf(),
    @SerialName("correctionsPolicy") val correctionsPolicy: String? = null,
    @SerialName("diversityPolicy") val diversityPolicy: String? = null,
    @SerialName("sameAs") val sameAs: List<String> = listOf(),
    @SerialName("logo") val logo: List<Logo> = listOf()
)
