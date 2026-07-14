package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.LogoDto
import de.visualdigits.newshomereader.data.model.applicationjson.LogoWrapper
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

object LogoWrapperSerializer : KSerializer<LogoWrapper> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): LogoWrapper {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            LogoWrapper(listOf(LogoDto(url = element.content)))
        } else if (element is JsonObject) {
            LogoWrapper(
                listOf(
                    decoder.json.decodeFromJsonElement(
                        element = element,
                        deserializer = LogoDto.serializer()
                    )
                )
            )
        } else if (element is JsonArray) {
            LogoWrapper(element.map { elem ->
                if (elem is JsonPrimitive && elem.isString) {
                    LogoDto(url = elem.content)
                } else if (elem is JsonObject) {
                    decoder.json.decodeFromJsonElement(element = elem, deserializer = LogoDto.serializer())
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
        value: LogoWrapper
    ) {
        // not needed
    }
}
