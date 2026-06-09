package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.opml.Body
import de.visualdigits.newshomereader.data.model.opml.Head
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.data.model.opml.Outline
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem

fun Opml.toNewsFeedConfiguration(): List<NewsFeedGroup> {
    val mainGroups = body?.outlines
        ?.map { mainGroupOutline ->
            val newsFeedItems = mainGroupOutline.outlines
                .filter { o -> o.outlines.isEmpty()}
                .map { feedOutline ->
                    NewsFeedItem(
                        mainGroupName = feedOutline.title?:"",
                        outlineType = feedOutline.outlineType,
                        name = feedOutline.title,
                        imageUrl = feedOutline.imageUrl,
                        url = feedOutline.xmlUrl,
                        stopWords = if (feedOutline.stopWords?.isNotEmpty() == true) feedOutline.stopWords.split(",").map { s -> s.trim() } else listOf()
                    )
                }
            val subGroups = mainGroupOutline.outlines
                .filter { o -> o.outlines.isNotEmpty()}
                .map { subGroupOutline ->
                val newsFeedItems = subGroupOutline.outlines
                    .filter { o -> o.outlines.isEmpty()}
                    .map { feedOutline ->
                        NewsFeedItem(
                            mainGroupName = feedOutline.title?:"",
                            outlineType = feedOutline.outlineType,
                            name = feedOutline.title,
                            imageUrl = feedOutline.imageUrl,
                            url = feedOutline.xmlUrl,
                            stopWords = if (feedOutline.stopWords?.isNotEmpty() == true) feedOutline.stopWords.split(",").map { s -> s.trim() } else listOf()
                        )
                    }
                NewsFeedGroup(
                    outlineType = subGroupOutline.outlineType,
                    name = subGroupOutline.title?:"",
                    parentGroupName = mainGroupOutline.title,
                    newsFeeds = newsFeedItems,
                )
            }
            NewsFeedGroup(
                outlineType = mainGroupOutline.outlineType,
                newsFeeds = newsFeedItems,
                name = mainGroupOutline.title?:"",
                subGroups = subGroups
            )
        } ?: listOf()

    return mainGroups
}

fun List<NewsFeedGroup>.toOpml(): Opml {
    return Opml(
        version = "1.1",
        head = Head(
            title = "NewsHomeReader"
        ),
        body = Body(
            outlines = this.map { mainGroup ->
                Outline(
                    title = mainGroup.name,
                    text = mainGroup.name,
                    outlineType = mainGroup.outlineType,
                    outlines = mainGroup.newsFeeds.map { newsFeed ->
                        createNewsFeedOutline(newsFeed)
                    } + mainGroup.subGroups.map { subGroup ->
                        Outline(
                            title = subGroup.name,
                            text = subGroup.name,
                            outlineType = subGroup.outlineType,
                            outlines = subGroup.newsFeeds.map { newsFeed ->
                                createNewsFeedOutline(newsFeed)
                            }
                        )
                    }
                )
            },
        )
    )
}

fun List<NewsFeedGroup>.mergeNewsFeedGroups(other: List<NewsFeedGroup>): List<NewsFeedGroup> {
    val lookupOther = other.associateBy { sg -> Pair(sg.name, sg.parentGroupName) }
    val lookup = associateBy { sg -> Pair(sg.name, sg.parentGroupName) }
    return mapNotNull { nfg -> lookupOther[Pair(nfg.name, nfg.parentGroupName)]?.let { onfg -> nfg.merge(onfg) } } +
            lookup.filter { (k, v) -> !lookupOther.contains(k) }.values +
            lookupOther.filter { (k, v) -> !lookup.contains(k) }.values

}

fun List<NewsFeedItem>.mergeNewsFeedItems(other: List<NewsFeedItem>): List<NewsFeedItem> {
    val lookupOther = other.associateBy { nfi -> Triple(nfi.name, nfi.mainGroupName, nfi.subGroupName) }
    val lookup = associateBy { nfi -> Triple(nfi.name, nfi.mainGroupName, nfi.subGroupName) }
    return mapNotNull { nfi -> lookupOther[Triple(nfi.name, nfi.mainGroupName, nfi.subGroupName)]?.let { onfi -> nfi.merge(onfi) } } +
            lookup.filter { (k, v) -> !lookupOther.contains(k) }.values +
            lookupOther.filter { (k, v) -> !lookup.contains(k) }.values
}

private fun createNewsFeedOutline(newsFeedItem: NewsFeedItem): Outline {
    return Outline(
        title = newsFeedItem.name,
        text = newsFeedItem.name,
        xmlUrl = newsFeedItem.url,
        type = "rss",
        outlineType = newsFeedItem.outlineType,
        imageUrl = newsFeedItem.imageUrl,
        stopWords = if (newsFeedItem.stopWords.isNotEmpty()) newsFeedItem.stopWords.filter { sw -> sw.isNotEmpty() }
            .joinToString(",") else null,
    )
}

