package de.visualdigits.newshomereader.data.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CopyrightHolderDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null
)
