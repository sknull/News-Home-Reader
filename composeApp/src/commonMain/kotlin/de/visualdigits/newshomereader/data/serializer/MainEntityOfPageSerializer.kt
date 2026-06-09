package de.visualdigits.newshomereader.data.serializer

import de.visualdigits.newshomereader.data.model.applicationjson.MainEntityOfPageDto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object MainEntityOfPageSerializer : KSerializer<MainEntityOfPageDto> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Image"
    ) {
        element<String>("url")
    }

    override fun deserialize(decoder: Decoder): MainEntityOfPageDto {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive && element.isString) {
            MainEntityOfPageDto(url = element.content)
        } else if (element is JsonObject) {
            decoder.json.decodeFromJsonElement(element = element, deserializer = MainEntityOfPageDto.serializer())
        } else {
            error("Unsupported element")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: MainEntityOfPageDto
    ) {
        // not needed
    }
}
