package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonDto
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonWrapper
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

class AppJsonWrapperSerializer : KSerializer<AppJsonWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): AppJsonWrapper {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            AppJsonWrapper(listOf(AppJsonDto(url = element.content)))
        } else if (element is JsonObject) {
            AppJsonWrapper(
                listOf(
                    decoder.json.decodeFromJsonElement(
                        element = element,
                        deserializer = AppJsonDto.serializer()
                    )
                )
            )
        } else if (element is JsonArray) {
            AppJsonWrapper(element.flatMap { elem ->
                if (elem is JsonPrimitive && elem.isString) {
                    listOf(AppJsonDto(url = elem.content))
                } else if (elem is JsonObject) {
                    listOf(decoder.json.decodeFromJsonElement(element = elem, deserializer = AppJsonDto.serializer()))
                } else if (elem is JsonArray) {
                    elem.map { e -> decoder.json.decodeFromJsonElement(element = e, deserializer = AppJsonDto.serializer()) }
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
        value: AppJsonWrapper
    ) {
        // not needed
    }
}
