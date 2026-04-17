package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MainEntityOfPageDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val url: String? = null,
    val breadcrumb: BreadcrumbDto? = null
)
