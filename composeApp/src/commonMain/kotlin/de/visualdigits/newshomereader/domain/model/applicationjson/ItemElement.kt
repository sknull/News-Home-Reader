package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ItemElement(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val containerId: String? = null,
    val position: Int? = null,
    val item: Item? = null
)
