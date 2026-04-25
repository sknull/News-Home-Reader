package de.visualdigits.newshomereader.domain.model.catalog

import kotlinx.serialization.Serializable

@Serializable
data class NewsFeedCatalogCategory(
    var parentCategoryName: String? = null,
    val name: String,
    val url: String,
    val feeds: List<NewsFeedCatalogItem> = listOf(),
    val subCategories: List<NewsFeedCatalogCategory> = listOf()
) {
    init {
        feeds.forEach { f -> f.parentCategoryName = name }
        subCategories.forEach { sc -> sc.parentCategoryName = name }
    }
}
