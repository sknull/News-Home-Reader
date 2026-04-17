package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class PotentialActionDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val target: TargetWrapper? = null,
    @SerialName("startOffset-input") val startOffsetInput: String? = null,
    @SerialName("query-input") val queryInput: QueryInputDto? = null
)
