package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CopyrightHolderDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null
)
