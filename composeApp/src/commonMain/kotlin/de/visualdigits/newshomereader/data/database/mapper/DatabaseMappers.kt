package de.visualdigits.newshomereader.data.database.mapper

import app.cash.sqldelight.ColumnAdapter
import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsItemEntity
import de.visualdigits.newshomereader.domain.model.applicationjson.AppJson
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.MediaType
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun NewsFeedGroup.toNewsFeedGroupEntity(): NewsFeedGroupEntity {
    return NewsFeedGroupEntity(
        id = id,
        name = name,
        newsFeeds = newsFeeds
    )
}

fun NewsFeedGroupEntity.toNewsFeedGroup(): NewsFeedGroup {
    return NewsFeedGroup(
        id = id,
        name = name,
        newsFeeds = newsFeeds
    )
}

fun NewsFeed.toNewsFeedEntity(): NewsFeedEntity {
    return NewsFeedEntity(
        id = id,
        identifier = identifier,
        feedName = feedName,
        title = title,
        description = description,
        link = link,
        image = image,
        imageTitle = imageTitle,
        imageCaption = imageCaption,
        updatedMillis = updated.toInstant().toEpochMilli(),
        updatedZone = updated.offset.id,
        rights = rights,
        language = language,
        keywords = keywords
    )
}

fun NewsFeedEntity.toNewsFeed(): NewsFeed {
    return NewsFeed(
        id = id,
        identifier = identifier,
        feedName = feedName,
        title = title,
        description = description,
        link = link,
        image = image,
        imageTitle = imageTitle,
        imageCaption = imageCaption,
        updated = OffsetDateTime.ofInstant(Instant.ofEpochMilli(updatedMillis), ZoneOffset.of(updatedZone)),
        rights = rights,
        language = language,
        keywords = keywords
    )
}

fun NewsItem.toNewsItemEntity(): NewsItemEntity {
    return NewsItemEntity(
        id = id,
        feedName = feedName,
        identifier = identifier,
        publishedMillis = published.toInstant().toEpochMilli(),
        publishedZone = published.offset.id,
        updatedMillis = updated.toInstant().toEpochMilli(),
        lastSeenMillis = System.currentTimeMillis(),
        updatedZone = updated.offset.id,
        link = link,
        title = title,
        summary = summary,
        content = content,
        keywords = keywords.joinToString(","),
        image = image,
        imageTitle = imageTitle,
        imageCaption = imageCaption,
        isRead = isRead
    )
}

fun NewsItemEntity.toNewsItem(): NewsItem {
    return NewsItem(
        id = id,
        feedName = feedName,
        identifier = identifier,
        published = OffsetDateTime.ofInstant(Instant.ofEpochMilli(publishedMillis), ZoneOffset.of(publishedZone)),
        updated = OffsetDateTime.ofInstant(Instant.ofEpochMilli(updatedMillis), ZoneOffset.of(updatedZone)),
        link = link,
        title = title,
        summary = summary,
        content = content,
        keywords = keywords.split(","),
        image = image,
        imageTitle = imageTitle,
        imageCaption = imageCaption,
        isRead = isRead
    )
}

val stringListAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> = databaseValue.split(",")
    override fun encode(value: List<String>): String = value.joinToString(",")
}

val intAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

val newsFeedsAdapter = object : ColumnAdapter<List<NewsFeedConfiguration>, String> {
    override fun decode(databaseValue: String): List<NewsFeedConfiguration> =
        if (databaseValue.isEmpty()) listOf() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<NewsFeedConfiguration>): String =
        Json.encodeToString(value)
}

val mediaItemAdapter = object : ColumnAdapter<List<MediaItem>, String> {
    override fun decode(databaseValue: String): List<MediaItem> =
        if (databaseValue.isEmpty()) listOf() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<MediaItem>): String =
        Json.encodeToString(value)
}

val applicationJsonAdapter = object : ColumnAdapter<List<AppJson>, String> {
    override fun decode(databaseValue: String): List<AppJson> =
        if (databaseValue.isEmpty()) emptyList() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<AppJson>): String =
        Json.encodeToString(value)
}

fun FullArticle.toFullArticleEntity(): FullArticleEntity {
    return FullArticleEntity(
        itemId = itemId,
        applicationJson = applicationJson,
        html = html,
        imageItems = imageItems,
        videoItems = videoItems,
        audioItems = audioItems,
        articleImage = articleImage,
        discussionUrl = discussionUrl,
        commentCount = commentCount,
        isFree = isFree,
        wordCount = wordCount,
        readingTime = readingTime
    )
}

fun FullArticleEntity.toFullArticle(): FullArticle {
    return FullArticle(
        itemId = itemId,
        applicationJson = applicationJson,
        html = html,
        imageItems = imageItems.map { mi -> mi.copy(type = MediaType.image) },
        videoItems = videoItems.map { mi -> mi.copy(type = MediaType.video) },
        audioItems = audioItems.map { mi -> mi.copy(type = MediaType.audio) },
        articleImage = articleImage,
        discussionUrl = discussionUrl,
        commentCount = commentCount,
        isFree = isFree,
        wordCount = wordCount,
        readingTime = readingTime
    )
}
