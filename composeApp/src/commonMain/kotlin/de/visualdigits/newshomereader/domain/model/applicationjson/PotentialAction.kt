package de.visualdigits.newshomereader.domain.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class PotentialAction(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val target: List<Target> = listOf(),
    @SerialName("startOffset-input") val startOffsetInput: String? = null,
    @SerialName("query-input") val queryInput: QueryInput? = null
)
