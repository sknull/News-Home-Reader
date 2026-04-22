package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.opml.Body
import de.visualdigits.newshomereader.data.model.opml.Head
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
                url = xmlUrl?:"",
                stopWords = stopWords?.split(",")?:listOf()
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

fun List<NewsFeedGroup>.toOpml(): Opml {
    return Opml(
        version = "1.1",
        head = Head(
            title = "NewsHomeReader"
        ),
        body = Body(
            outlines = this.map { group ->
                Outline(
                    title = group.name,
                    text = group.name,
                    outlines = group.newsFeeds.map { item ->
                        Outline(
                            title = item.name,
                            text = item.name,
                            xmlUrl = item.url,
                            type = "rss",
                            imageUrl = item.imageUrl,
                            stopWords = if (item.stopWords?.isNotEmpty() == true) item.stopWords.filter { sw -> sw.isNotEmpty() }.joinToString(",") else null
                        )
                    }
                )
            },
        )
    )
}
