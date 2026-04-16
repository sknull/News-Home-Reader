package de.visualdigits.newshomereader.domain.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IsPartOf(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: List<String> = listOf(),
    val name: String? = null,
    val productID: String? = null,
)
