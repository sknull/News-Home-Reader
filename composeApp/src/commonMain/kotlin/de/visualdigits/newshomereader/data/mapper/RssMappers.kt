package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.rss.Item
import de.visualdigits.newshomereader.data.model.rss.Rss
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.extractImage
import java.io.File
import java.net.URI
import java.time.OffsetDateTime


fun Item.toNewsItem(feedName: String): NewsItem {
    var (image, imageTitle, imageCaption) = content?.let { c -> extractImage(c) }?:Triple(null, null, null)
    if (image == null) {
        image = enclosure?.url
    }

    return NewsItem(
        feedName = feedName,
        identifier = identifier ?: id ?: link?.let { l -> File(URI(l).path).nameWithoutExtension } ?: error("No identifier"),
        published = date ?: pubDate ?: OffsetDateTime.MIN,
        updated = pubDate ?: OffsetDateTime.MIN,
        link = link ?: "",
        title = title?.trim() ?: "",
        summary = description?.trim() ?: "",
        content = encoded?:"",
        keywords = categories.map { category -> category.text }.filter { c -> c.isNotEmpty() },
        image = image ?: "",
        imageTitle = imageTitle ?: "",
        imageCaption = imageCaption ?: ""
    )
}


fun Rss.toNewsFeed(feedName: String): NewsFeed {
    return NewsFeed(
        identifier = channel?.title?:"",
        feedName = feedName,
        title = channel?.title ?: "",
        description = channel?.description ?: "",
        link = channel?.link ?: "",
        image = channel?.image?.url ?: "",
        imageTitle = channel?.image?.title ?: "",
        imageCaption = channel?.image?.caption ?: "",
        updated = channel?.lastBuildDate ?: OffsetDateTime.MIN,
        rights = channel?.rights ?: "",
        language = channel?.language ?: "",
        items = (items?.map { item -> item.toNewsItem(feedName) }
            ?: channel?.items?.map { item -> item.toNewsItem(feedName) }
            ?: listOf()).distinct()
    )
}
