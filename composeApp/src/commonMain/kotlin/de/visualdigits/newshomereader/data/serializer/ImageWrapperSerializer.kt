package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.ImageDto
import de.visualdigits.newshomereader.data.model.applicationjson.ImageWrapper
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object ImageWrapperSerializer : KSerializer<ImageWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): ImageWrapper {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Unknown decoder")
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            ImageWrapper(listOf(ImageDto(url = listOf(element.content))))
        } else if (element is JsonObject) {
            ImageWrapper(
                listOf(
                    decoder.json.decodeFromJsonElement(
                        element = element,
                        deserializer = ImageDto.serializer()
                    )
                )
            )
        } else if (element is JsonArray) {
            ImageWrapper(element.map { elem ->
                if (elem is JsonPrimitive && elem.isString) {
                    ImageDto(url = listOf(elem.content))
                } else if (elem is JsonObject) {
                    decoder.json.decodeFromJsonElement(element = elem, deserializer = ImageDto.serializer())
                } else {
                    error("Unsupported element")
                }
            })
        } else {
            error("Unsupported element")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: ImageWrapper
    ) {
        // not needed
    }
}
