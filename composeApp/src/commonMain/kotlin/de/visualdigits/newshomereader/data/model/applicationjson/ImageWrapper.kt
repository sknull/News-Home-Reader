package de.visualdigits.newshomereader.data.model.applicationjson

import de.visualdigits.newshomereader.data.serializer.ImageWrapperSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ImageWrapperSerializer::class)
data class ImageWrapper(
    val images: List<ImageDto> = listOf()
)
