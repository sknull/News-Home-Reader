package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.TargetWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TargetWrapperSerializer::class)
@Immutable
data class TargetWrapper(
    val targets: List<TargetDto> = listOf()
)
