package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.data.serializer.VideoWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = VideoWrapperSerializer::class)
@Immutable
data class VideoWrapper(
    @Serializable(with = ListSerializer::class) val videos: List<VideoDto> = listOf()
)
