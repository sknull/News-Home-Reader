package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.data.serializer.TargetWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TargetWrapperSerializer::class)
@Immutable
data class TargetWrapper(
    @Serializable(with = ListSerializer::class) val targets: List<TargetDto> = listOf()
)
