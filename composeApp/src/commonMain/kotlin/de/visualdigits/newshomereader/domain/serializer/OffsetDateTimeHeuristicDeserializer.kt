package de.visualdigits.newshomereader.domain.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object OffsetDateTimeHeuristicDeserializer : KSerializer<OffsetDateTime> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = OffsetDateTime::class.simpleName!!
    ) {
        element<String>("dateTime")
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return parse(decoder.decodeString())
    }

    fun parse(text: String): OffsetDateTime {
        val time = (parseDateOnly(text)
            ?: parseRfc1123EN(text)
            ?: parseRfc1123DE(text)
            ?: parseOffsetDateTimeWithMillis(text)
            ?: parseOffsetDateTimeWithoutMillis(
                text
            )
            ?: OffsetDateTime.now())
        return time
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.format( DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")))
    }

    private fun parseRfc1123EN(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.ENGLISH))
        } catch (_: Exception) {
            null
        }
    }

    private fun parseRfc1123DE(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.GERMAN))
        } catch (_: Exception) {
            null
        }
    }

    private fun parseOffsetDateTimeWithMillis(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
        } catch (_: Exception) {
            null // by means
        }
    }

    private fun parseOffsetDateTimeWithoutMillis(text: String): OffsetDateTime? {
        return try {
            OffsetDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
        } catch (_: Exception) {
            null // by means
        }
    }

    private fun parseDateOnly(text: String): OffsetDateTime? {
        return try {
            LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay(ZoneOffset.UTC)
                .toOffsetDateTime()
        } catch (_: Exception) { null }
    }
}
