package de.visualdigits.newshomereader.domain.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class SourceOrganization(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val identifier: String? = null,
    val name: String? = null
)
