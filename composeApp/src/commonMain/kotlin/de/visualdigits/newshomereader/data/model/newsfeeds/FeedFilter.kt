package de.visualdigits.newshomereader.data.model.newsfeeds

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FeedFilter(
    val stopWords: List<String> = listOf()
)
