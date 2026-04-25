package de.visualdigits.newshomereader.domain.model.catalog

import kotlinx.serialization.Serializable

@Serializable
data class NewsFeedCatalogCategory(
    var parentCategory: NewsFeedCatalogCategory? = null,
    val name: String,
    val url: String,
    val feeds: List<NewsFeedCatalogItem> = listOf(),
    val subCategories: List<NewsFeedCatalogCategory> = listOf()
) {
    init {
        feeds.forEach { f -> f.parentCategory = this }
        subCategories.forEach { sc -> sc.parentCategory = this }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewsFeedCatalogCategory) return false

        return name == other.name &&
                url == other.url &&
                feeds == other.feeds &&
                subCategories == other.subCategories
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + feeds.hashCode()
        result = 31 * result + subCategories.hashCode()
        return result
    }

    override fun toString(): String {
        return "NewsFeedCatalogCategory(name='$name', url='$url', feeds=$feeds, subCategories=$subCategories)"
    }
}
