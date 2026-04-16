package de.visualdigits.newshomereader.domain.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Breadcrumb(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    val itemListElement: List<ItemElement> = listOf()
)
