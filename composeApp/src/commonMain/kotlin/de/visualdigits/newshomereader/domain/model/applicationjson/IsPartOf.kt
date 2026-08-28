package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class IsPartOf(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: List<String> = listOf(),
    @SerialName("name") val name: String? = null,
    @SerialName("productID") val productID: String? = null,
)
