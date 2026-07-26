package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.atom.Entry
import de.visualdigits.newshomereader.data.model.atom.Feed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.extractImage


fun Entry.toNewsItem(feedName: String): NewsItem {
    val content = content?.trim()
    val (image, imageTitle, imageCaption) = extractImage(content?:"")
    return NewsItem(
        feedName = feedName,
        identifier = id ?: link?.href ?: "${feedName}_${title}_$published",
        published = published,
        updated = updated,
        link = link?.href ?: "",
        title = title?.trim() ?: "",
        summary = summary?.trim() ?: "",
        content = content?:"",
        keywords = keywords ?: listOf(),
        image = image ?: "",
        imageTitle = imageTitle ?: "",
        imageCaption = imageCaption ?: ""
    )
}

fun Feed.toNewsFeed(feedName: String): NewsFeed {
    return NewsFeed(
        identifier = id ?: "",
        feedName = feedName,
        title = title?.trim() ?: "",
        description = subtitle?.text?.trim() ?: "",
        link = links?.firstOrNull()?.href ?: "",
        image = "",
        imageTitle = "",
        imageCaption = "",
        updated = updated,
        rights = rights ?: "",
        language = "",
        keywords = keywords ?: listOf(),
        items = entries
            ?.map { entry -> entry.toNewsItem(feedName) }
            ?: listOf()
    )
}
