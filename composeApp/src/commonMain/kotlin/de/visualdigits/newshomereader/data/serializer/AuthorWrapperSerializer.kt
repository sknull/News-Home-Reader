package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.AuthorDto
import de.visualdigits.newshomereader.data.model.applicationjson.AuthorWrapper
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

object AuthorWrapperSerializer : KSerializer<AuthorWrapper> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): AuthorWrapper {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            AuthorWrapper(listOf(AuthorDto(url = element.content)))
        } else if (element is JsonObject) {
            AuthorWrapper(
                listOf(
                    decoder.json.decodeFromJsonElement(
                        element = element,
                        deserializer = AuthorDto.serializer()
                    )
                )
            )
        } else if (element is JsonArray) {
            AuthorWrapper(element.flatMap { elem ->
                if (elem is JsonPrimitive && elem.isString) {
                    listOf(AuthorDto(url = elem.content))
                } else if (elem is JsonObject) {
                    listOf(decoder.json.decodeFromJsonElement(element = elem, deserializer = AuthorDto.serializer()))
                } else if (elem is JsonArray) {
                    elem.map { e -> decoder.json.decodeFromJsonElement(element = e, deserializer = AuthorDto.serializer()) }
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
        value: AuthorWrapper
    ) {
        // not needed
    }
}
