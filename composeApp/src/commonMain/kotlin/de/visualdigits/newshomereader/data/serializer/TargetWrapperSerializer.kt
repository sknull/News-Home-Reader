package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.TargetDto
import de.visualdigits.newshomereader.data.model.applicationjson.TargetWrapper
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

object TargetWrapperSerializer : KSerializer<TargetWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): TargetWrapper {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            TargetWrapper(listOf(TargetDto(urlTemplate = element.content)))
        } else if (element is JsonObject) {
            TargetWrapper(
                listOf(
                    decoder.json.decodeFromJsonElement(
                        element = element,
                        deserializer = TargetDto.serializer()
                    )
                )
            )
        } else if (element is JsonArray) {
            TargetWrapper(element.map { elem ->
                if (elem is JsonPrimitive && elem.isString) {
                    TargetDto(urlTemplate = elem.content)
                } else if (elem is JsonObject) {
                    decoder.json.decodeFromJsonElement(element = elem, deserializer = TargetDto.serializer())
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
        value: TargetWrapper
    ) {
        // not needed
    }
}
