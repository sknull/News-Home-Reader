package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Breadcrumb(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("itemListElement") val itemListElement: List<ItemElement> = listOf()
)
