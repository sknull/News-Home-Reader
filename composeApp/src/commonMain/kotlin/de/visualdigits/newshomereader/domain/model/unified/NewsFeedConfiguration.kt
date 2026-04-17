package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.model.newsfeeds.FeedFilter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
@Immutable
data class NewsFeedConfiguration(
    val name: String,
    val groupName: String,
    val imageUrl: String? = null,
    val url: String = "",
    val filters: Map<String, FeedFilter> = mapOf()
) {

    companion object {

        val mapper = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = false
        }

        fun decodeFromString(json: String): NewsFeedConfiguration {
            return mapper.decodeFromString(json)
        }

        fun decodeValue(file: File): NewsFeedConfiguration {
            return mapper.decodeFromString(file.readText())
        }
    }

    override fun toString(): String {
        return "$name: $url"
    }
}
