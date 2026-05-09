package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.VideoDto
import de.visualdigits.newshomereader.data.model.applicationjson.VideoWrapper
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

object VideoWrapperSerializer : KSerializer<VideoWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): VideoWrapper {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            VideoWrapper(listOf(VideoDto(contentUrl = listOf(element.content))))
        } else if (element is JsonObject) {
            VideoWrapper(
                listOf(
                    decoder.json.decodeFromJsonElement(
                        element = element,
                        deserializer = VideoDto.serializer()
                    )
                )
            )
        } else if (element is JsonArray) {
            VideoWrapper(element.map { elem ->
                if (elem is JsonPrimitive && elem.isString) {
                    VideoDto(contentUrl = listOf(elem.content))
                } else if (elem is JsonObject) {
                    decoder.json.decodeFromJsonElement(element = elem, deserializer = VideoDto.serializer())
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
        value: VideoWrapper
    ) {
        // not needed
    }
}
