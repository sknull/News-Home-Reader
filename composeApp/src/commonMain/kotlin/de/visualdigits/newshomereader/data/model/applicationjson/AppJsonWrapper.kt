package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.AppJsonWrapperSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable(with = AppJsonWrapperSerializer::class)
@Immutable
data class AppJsonWrapper(
    val appJsons: List<AppJsonDto> = listOf()
) {

    companion object {

        val mapper = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            explicitNulls = false
            encodeDefaults = false
        }

        fun decodeFromString(json: String): AppJsonWrapper {
            return mapper.decodeFromString(json)
        }

        fun decodeValue(file: File): AppJsonWrapper {
            return mapper.decodeFromString(file.readText())
        }
    }
}
