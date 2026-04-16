package de.visualdigits.newshomereader.domain.model.applicationjson

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Target(
    @SerialName("@type") val type: String? = null,
    @SerialName("urlTemplate") val urlTemplate: String? = null
) {

    constructor(
        urlTemplate: String? = null
    ): this(null, urlTemplate)
}
