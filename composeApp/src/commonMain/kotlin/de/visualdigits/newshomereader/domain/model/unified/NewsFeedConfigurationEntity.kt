package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
@Immutable
data class NewsFeedConfigurationEntity(
    val name: String? = null,
    val groupName: String? = null,
    val imageUrl: String? = null,
    val url: String? = null,
    val stopWords: List<String>? = listOf()
) {

    companion object {

        val mapper = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = false
        }

        fun decodeFromString(json: String): NewsFeedConfigurationEntity {
            return mapper.decodeFromString(json)
        }

        fun decodeValue(file: File): NewsFeedConfigurationEntity {
            return mapper.decodeFromString(file.readText())
        }
    }

    override fun toString(): String {
        return "$name: $url"
    }
}
