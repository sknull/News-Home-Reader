package de.visualdigits.newshomereader.domain.model.catalog

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class NewsFeedCatalogItem(
    @Transient var parentCategory: NewsFeedCatalogCategory? = null,
    val name: String,
    val descriptionShort: String,
    val descriptionLong: String,
    val url: String
) {

    val rootLine: String
        get() {
            val rootCategory = parentCategory?.parentCategory?.name
            val parentCategory = parentCategory?.name
            return "${rootCategory?.let{"${it}_"}}${parentCategory?.let{"${it}_"}}$name"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewsFeedCatalogItem) return false

        return name == other.name &&
                descriptionShort == other.descriptionShort &&
                descriptionLong == other.descriptionLong &&
                url == other.url
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + descriptionShort.hashCode()
        result = 31 * result + descriptionLong.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }

    override fun toString(): String {
        return "NewsFeedCatalogItem(name='$name', descriptionShort='$descriptionShort', descriptionLong='$descriptionLong', url='$url')"
    }
}
