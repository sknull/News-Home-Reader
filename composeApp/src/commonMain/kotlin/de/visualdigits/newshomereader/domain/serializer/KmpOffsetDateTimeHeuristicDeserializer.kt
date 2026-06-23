package de.visualdigits.newshomereader.domain.serializer

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.atTime
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.toInstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object KmpOffsetDateTimeHeuristicDeserializer : KSerializer<KmpOffsetDateTime> {

    // Für einen einzelnen String-Wert reicht ein PrimitiveSerialDescriptor
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "KmpOffsetDateTime",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): KmpOffsetDateTime {
        return parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: KmpOffsetDateTime) {
        encoder.encodeString(value.toString()) // Nutzt Ihre toString() Logik mit Offset
    }

    fun parse(text: String): KmpOffsetDateTime {
        return parseOffsetDateTimeWithMillis(text)
            ?: parseOffsetDateTimeWithoutMillis(text)
            ?: parseDateOnly(text)
            ?: parseRfc1123(text)
            ?: KmpOffsetDateTime.now()
    }

    // ISO Format mit optionalen Millisekunden und Offset (z.B. +02:00 oder Z)
    private val formatIso = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET

    // RFC 1123 benötigt im KMP-Format keine Locale, da die englischen Kürzel fest verbaut sind
    private val formatRfc1123 = DateTimeComponents.Formats.RFC_1123

    private fun parseOffsetDateTimeWithMillis(text: String): KmpOffsetDateTime? {
        return try {
            val components = formatIso.parse(text)
            val instant = components.toInstantUsingOffset()
            val offset = components.toUtcOffset()
            KmpOffsetDateTime(instant, offset)
        } catch (_: Exception) { null }
    }

    private fun parseOffsetDateTimeWithoutMillis(text: String): KmpOffsetDateTime? {
        // Hinweis: Das Standard ISO_DATE_TIME Format von kotlinx-datetime
        // fängt bereits Versionen mit UND ohne Millisekunden automatisch ab.
        return parseOffsetDateTimeWithMillis(text)
    }

    private fun parseDateOnly(text: String): KmpOffsetDateTime? {
        return try {
            val localDate = LocalDate.parse(text) // Erwartet ISO-Date (YYYY-MM-DD)
            val localDateTime = localDate.atTime(0, 0)
            val instant = localDateTime.toInstant(TimeZone.UTC)
            KmpOffsetDateTime(instant, UtcOffset.ZERO)
        } catch (_: Exception) { null }
    }

    private fun parseRfc1123(text: String): KmpOffsetDateTime? {
        return try {
            val components = formatRfc1123.parse(text)
            val instant = components.toInstantUsingOffset()
            val offset = components.toUtcOffset()
            KmpOffsetDateTime(instant, offset)
        } catch (_: Exception) { null }
    }
}
