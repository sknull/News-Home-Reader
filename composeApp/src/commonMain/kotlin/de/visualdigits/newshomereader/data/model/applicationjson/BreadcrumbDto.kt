package de.visualdigits.newshomereader.data.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BreadcrumbDto(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    val itemListElement: List<ItemElementDto> = listOf()
)
