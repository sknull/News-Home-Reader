package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.data.model.opml.Outline
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup

fun Opml.toNewsFeedConfiguration(): List<NewsFeedGroup> {
    return body?.outlines
        ?.flatMap { outline ->
            outline.toNewsFeedConfiguration(null)
        }
        ?: listOf()
}

fun Outline.toNewsFeedConfiguration(parent: Outline? = null, newsFeedGroups: MutableMap<String, NewsFeedGroup> = mutableMapOf()): List<NewsFeedGroup> {
    val name = title?.replace("\n", "")?.trim() ?: ""
    if (outlines.isEmpty()) {
        val parentName = parent?.title?.replace("\n", "")?.trim() ?: ""
        val group = newsFeedGroups[parentName]
        if (group != null) {
            val node = NewsFeedConfigurationEntity(
                name = name,
                groupName = parentName,
                imageUrl = imageUrl,
                url = xmlUrl?:""
            )
            newsFeedGroups[parentName] = group.copy(
                newsFeeds = group.newsFeeds + node
            )
        }
    } else {
        newsFeedGroups.computeIfAbsent(name) { NewsFeedGroup(name = name) }
        outlines.forEach { o -> o.toNewsFeedConfiguration(this, newsFeedGroups) }
    }

    return newsFeedGroups.values.toList()
}
