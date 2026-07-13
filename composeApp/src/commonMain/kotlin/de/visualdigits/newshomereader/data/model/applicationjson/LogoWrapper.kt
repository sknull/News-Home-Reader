package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.data.serializer.LogoWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LogoWrapperSerializer::class)
@Immutable
data class LogoWrapper(
    @Serializable(with = ListSerializer::class) val logos: List<LogoDto> = listOf()
)
