package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class NewsFeedGroup(
    val id: Long = 0L,
    val parentId: Long? = null,
    var parentGroupName: String? = null,
    val name: String,
    val newsFeeds: List<NewsFeedItem> = listOf(),
    val subGroups: List<NewsFeedGroup> = listOf()
) {
    init {
        newsFeeds.forEach { f -> f.parentGroupName = name }
        subGroups.forEach { sc -> sc.parentGroupName = name }
    }

    override fun toString(): String {
        return "NewsFeedGroup(id=$id, name='$name', newsFeeds=$newsFeeds, subGroups=$subGroups)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewsFeedGroup) return false

        return parentGroupName == other.parentGroupName &&
                name == other.name &&
                newsFeeds == other.newsFeeds &&
                subGroups == other.subGroups
    }

    override fun hashCode(): Int {
        var result = parentGroupName?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + newsFeeds.hashCode()
        result = 31 * result + subGroups.hashCode()
        return result
    }
}
