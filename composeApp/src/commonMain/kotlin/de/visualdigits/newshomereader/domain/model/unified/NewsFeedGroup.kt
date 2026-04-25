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
}
