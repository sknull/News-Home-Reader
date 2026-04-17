package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class About(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: List<String> = listOf(),
    val sameAs: List<String> = listOf()
)
