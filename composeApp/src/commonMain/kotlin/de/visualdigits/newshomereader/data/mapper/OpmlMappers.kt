package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.data.model.newsfeeds.NodeType
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.data.model.opml.Outline

fun Outline.toNewsFeedConfiguration(parent: NewsFeedConfigurationEntity? = null): NewsFeedConfigurationEntity {
    val node = NewsFeedConfigurationEntity(
        name = title?.replace("\n", "")?.trim()?:"",
        type = if (outlines.isNotEmpty()) NodeType.folder else NodeType.leaf,
        parent = parent,
        imageUrl = imageUrl,
        url = xmlUrl?:""
    )
    node.children = outlines.map { o -> o.toNewsFeedConfiguration(node) }

    return node
}

fun Opml.toNewsFeedConfiguration(): NewsFeedConfigurationEntity {
    return NewsFeedConfigurationEntity(
        name = head?.title?.replace("\n", "")?.trim()?:"",
        type = NodeType.folder,
        children = body?.outlines?.map { o -> o.toNewsFeedConfiguration() } ?: listOf(),
    )
}
