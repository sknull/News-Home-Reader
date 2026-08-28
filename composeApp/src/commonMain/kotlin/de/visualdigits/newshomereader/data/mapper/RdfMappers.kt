package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.newshomereader.data.model.rdf.Item
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.extractImage


fun Item.toNewsItem(feedName: String): NewsItem {
    var (image, imageTitle, imageCaption) = content?.let { c -> extractImage(c) }?:Triple(null, null, null)

    return NewsItem(
        feedName = feedName,
        identifier = identifier ?: link ?: "${feedName}_${title}_$pubDate",
        published = date,
        updated = pubDate,
        link = link ?: "",
        title = title?.trim() ?: "",
        summary = description?.trim() ?: "",
        content = encoded?:"",
        image = image ?: "",
        imageTitle = imageTitle ?: "",
        imageCaption = imageCaption ?: ""
    )
}


fun Rdf.toNewsFeed(feedName: String): NewsFeed {
    val newsFeed = NewsFeed(
        identifier = channel?.title?:"",
        feedName = feedName,
        title = channel?.title ?: "",
        description = channel?.description?:"",
        link = channel?.link?:"",
        image = channel?.image?.resource?:"",
        imageTitle = "",
        imageCaption = "",
        updated = channel?.lastBuildDate ?: KmpOffsetDateTime.MIN,
        rights = channel?.rights?:"",
        language = channel?.language?:"",
        items = (items?.map { item -> item.toNewsItem(feedName) }
            ?: listOf()).distinct()
    )

    return newsFeed
}
