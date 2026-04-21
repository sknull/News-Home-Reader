package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable

@Immutable
data class NewsFeedGroup(
    val id: Long = 0L,
    val name: String,
    val newsFeeds: List<NewsFeedConfigurationEntity> = listOf(),
)
