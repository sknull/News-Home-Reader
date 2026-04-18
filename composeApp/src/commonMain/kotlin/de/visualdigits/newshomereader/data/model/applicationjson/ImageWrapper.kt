package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ImageWrapperSerializer
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ImageWrapperSerializer::class)
@Immutable
data class ImageWrapper(
    @Serializable(with = ListSerializer::class) val images: List<ImageDto> = listOf()
)
