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
                            name = feedOutline.title,
                            imageUrl = feedOutline.imageUrl,
                            url = feedOutline.xmlUrl,
                            stopWords = if (feedOutline.stopWords?.isNotEmpty() == true) feedOutline.stopWords.split(",").map { s -> s.trim() } else listOf()
                        )
                    }
                NewsFeedGroup(
                    name = subGroupOutline.title?:"",
                    parentGroupName = mainGroupOutline.title,
                    newsFeeds = newsFeedItems,
                )
            }
            NewsFeedGroup(
                name = mainGroupOutline.title?:"",
                newsFeeds = newsFeedItems,
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
                    outlines = mainGroup.newsFeeds.map { newsFeed ->
                        createNewsFeedOutline(newsFeed)
                    } + mainGroup.subGroups.map { subGroup ->
                        Outline(
                            title = subGroup.name,
                            text = subGroup.name,
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

private fun createNewsFeedOutline(newsFeed: NewsFeedItem): Outline = Outline(
    title = newsFeed.name,
    text = newsFeed.name,
    xmlUrl = newsFeed.url,
    type = "rss",
    imageUrl = newsFeed.imageUrl,
    stopWords = if (newsFeed.stopWords?.isNotEmpty() == true) newsFeed.stopWords.filter { sw -> sw.isNotEmpty() }
        .joinToString(",") else null,
)

