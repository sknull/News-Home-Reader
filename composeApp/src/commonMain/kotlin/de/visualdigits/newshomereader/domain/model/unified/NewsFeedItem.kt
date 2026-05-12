package de.visualdigits.newshomereader.domain.model.unified

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class NewsFeedItem(
    var mainGroupName: String,
    var subGroupName: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val url: String? = null,
    val stopWords: List<String> = listOf()
) {
    companion object {

        val mapper = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = false
        }

        fun decodeFromString(json: String): NewsFeedItem {
            return mapper.decodeFromString(json)
        }

        fun decodeValue(file: File): NewsFeedItem {
            return mapper.decodeFromString(file.readText())
        }
    }

    val rootLine: String
        get() = "${mainGroupName.let{"${it}_"}}${subGroupName?.let{"${it}_"}}$name"

    override fun toString(): String {
        return "$name: $url"
    }
}
