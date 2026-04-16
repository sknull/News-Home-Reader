package de.visualdigits.newshomereader.data.model.newsfeeds

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class NewsFeedConfigurationEntity(
    val name: String,
    val type: NodeType,
    @Transient var parent: NewsFeedConfigurationEntity? = null,
    var children: List<NewsFeedConfigurationEntity> = listOf(),
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

    /**
     * Returns all leaf nodes
     */
    fun getNewsFeeds(feeds: MutableList<NewsFeedConfigurationEntity> = mutableListOf()): List<NewsFeedConfigurationEntity> {
        feeds.addAll(children.filter { c ->  c.type == NodeType.leaf })
        children
            .filter { c ->  c.type == NodeType.folder }
            .forEach { c -> c.getNewsFeeds(feeds) }

        return feeds
    }
}
