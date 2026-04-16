package de.visualdigits.newshomereader.domain.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Publisher(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val url: String? = null,
    val alternateName: List<String> = listOf(),
    val correctionsPolicy: String? = null,
    val diversityPolicy: String? = null,
    val sameAs: List<String> = listOf(),
    val logo: Logo? = null
)
