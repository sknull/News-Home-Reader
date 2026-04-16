package de.visualdigits.newshomereader.data.model.applicationjson

import de.visualdigits.newshomereader.data.serializer.TargetWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TargetWrapperSerializer::class)
data class TargetWrapper(
    val targets: List<TargetDto> = listOf()
)
