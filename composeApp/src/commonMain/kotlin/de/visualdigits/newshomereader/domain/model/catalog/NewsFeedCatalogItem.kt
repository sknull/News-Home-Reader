package de.visualdigits.newshomereader.domain.model.catalog

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class NewsFeedCatalogItem(
    var parentCategoryName: String? = null,
    val name: String,
    val descriptionShort: String,
    val descriptionLong: String,
    val url: String
)
