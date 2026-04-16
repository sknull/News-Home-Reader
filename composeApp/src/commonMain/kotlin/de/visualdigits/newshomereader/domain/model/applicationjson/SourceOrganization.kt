package de.visualdigits.newshomereader.domain.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SourceOrganization(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val identifier: String? = null,
    val name: String? = null
)
