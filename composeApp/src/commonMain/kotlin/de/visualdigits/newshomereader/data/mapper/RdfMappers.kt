package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.rdf.Item
import de.visualdigits.newshomereader.data.model.rdf.Rdf
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.extractImage
import java.io.File
import java.net.URI
import java.time.OffsetDateTime


fun Item.toNewsItem(feedName: String): NewsItem {
    var (image, imageTitle, imageCaption) = content?.let { c -> extractImage(c) }?:Triple(null, null, null)

    return NewsItem(
        feedName = feedName,
        identifier = identifier ?: link?.let { l -> File(URI(l).path).nameWithoutExtension } ?: "${feedName}_${title}_$pubDate",
        published = date ?: pubDate ?: OffsetDateTime.MIN,
        updated = pubDate ?: OffsetDateTime.MIN,
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
        updated = channel?.lastBuildDate ?: OffsetDateTime.MIN,
        rights = channel?.rights?:"",
        language = channel?.language?:"",
        items = (items?.map { item -> item.toNewsItem(feedName) }
            ?: listOf()).distinct()
    )

    return newsFeed
}
