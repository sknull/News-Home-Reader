package de.visualdigits.newshomereader.data.serializer

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

class ListSerializer<T : Any>(
    private val dataSerializer: KSerializer<T>
) : KSerializer<List<T>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "List<T>"
    ) {
        element<String>("elem")
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): List<T> {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Unknown decoder")
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonObject) {
            listOf(decoder.json.decodeFromJsonElement(element = element, deserializer = dataSerializer))
        } else if (element is JsonArray) {
            element.map { elem -> decoder.json.decodeFromJsonElement(element = elem, deserializer = dataSerializer) }
        } else if (element is JsonPrimitive) {
            listOf(element.content as T)
        }
        else {
            error("Unsupported element")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<T>
    ) {
        // not needed
    }
}
