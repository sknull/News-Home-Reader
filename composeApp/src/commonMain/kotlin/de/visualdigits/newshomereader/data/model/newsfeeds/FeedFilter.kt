package de.visualdigits.newshomereader.data.model.newsfeeds

import kotlinx.serialization.Serializable

@Serializable
data class FeedFilter(
    val stopWords: List<String> = listOf()
)
