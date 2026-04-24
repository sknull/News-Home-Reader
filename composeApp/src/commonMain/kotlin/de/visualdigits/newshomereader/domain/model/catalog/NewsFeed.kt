package de.visualdigits.newshomereader.domain.model.catalog

import kotlinx.serialization.Serializable

@Serializable
data class NewsFeed(
    val name: String,
    val descriptionShort: String,
    val descriptionLong: String,
    val url: String
)
