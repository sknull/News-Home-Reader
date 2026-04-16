package de.visualdigits.newshomereader.domain.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class About(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: List<String> = listOf(),
    val sameAs: List<String> = listOf()
)
