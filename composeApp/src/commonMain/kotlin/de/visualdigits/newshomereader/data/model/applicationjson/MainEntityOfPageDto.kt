package de.visualdigits.newshomereader.data.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainEntityOfPageDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val url: String? = null,
    val breadcrumb: BreadcrumbDto? = null
)
