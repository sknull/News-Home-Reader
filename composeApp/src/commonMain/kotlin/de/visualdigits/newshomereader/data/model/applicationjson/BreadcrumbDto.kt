package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class BreadcrumbDto(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    val itemListElement: List<ItemElementDto> = listOf()
)
