package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class NewsFeedGroup(
    val id: Long = 0L,
    val name: String,
    val newsFeeds: List<NewsFeedConfigurationEntity> = listOf(),
    val subGroups: List<NewsFeedGroup> = listOf()
)
