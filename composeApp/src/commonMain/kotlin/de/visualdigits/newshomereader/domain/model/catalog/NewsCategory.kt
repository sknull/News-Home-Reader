package de.visualdigits.newshomereader.domain.model.catalog

import kotlinx.serialization.Serializable

@Serializable
data class NewsCategory(
    val name: String,
    val url: String,
    val feeds: List<NewsFeed> = listOf(),
    val subCategories: List<NewsCategory> = listOf()
)
