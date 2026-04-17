package de.visualdigits.newshomereader.domain.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Target(
    @SerialName("@type") val type: String? = null,
    @SerialName("urlTemplate") val urlTemplate: String? = null
) {

    constructor(
        urlTemplate: String? = null
    ): this(null, urlTemplate)
}
