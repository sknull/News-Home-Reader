package de.visualdigits.newshomereader.domain.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainEntityOfPage(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val url: String? = null,
    val breadcrumb: Breadcrumb? = null
)
