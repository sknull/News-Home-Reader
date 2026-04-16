package de.visualdigits.newshomereader.data.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IsPartOfDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: List<String> = listOf(),
    val name: String? = null,
    val productID: String? = null,
)
