package de.visualdigits.newshomereader.data.database

import app.cash.sqldelight.ColumnAdapter
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.GetAllNewsItemsWithArticles
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsItemEntity
import de.visualdigits.newshomereader.SearchNewsItems
import de.visualdigits.newshomereader.SettingsEntity
import de.visualdigits.newshomereader.domain.model.applicationjson.AppJson
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.MediaType
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun Settings.toSettingsEntity(): SettingsEntity {
    val settingsEntity = SettingsEntity(
        id = 0,
        displayTheme = get<DisplayThemeEnum>(SK.displayTheme)?.name ?: "LIGHT",
        clockColor = get<HsvColor>(SK.clockColor)?.hex() ?: "",
        spotColor = get<HsvColor>(SK.spotColor)?.hex() ?: "",
        language = get<Language>(SK.language)?.name ?: "EN",
        hideRead = get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
        loadArticles = get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false,
        refreshInterval = get<RefreshIntervalEnum>(SK.refreshInterval)?.name ?: "MINUTES_60",
        refreshWifiOnly = get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false,
        lastMaxImageSize = get<Int>(SK.maxImageSize)?.toLong() ?: 1200L,
        keepReadArticles = get<KeepArticlesEnum>(SK.keepReadArticles)?.name ?: "DAYS_30",
        keepUnreadArticles = get<KeepArticlesEnum>(SK.keepUnreadArticles)?.name ?: "DAYS_30",
        webDavUrl = get<String>(SK.webDavUrl) ?: "",
        webDavDirectory = get<String>(SK.webDavDirectory) ?: "",
        webDavUser = get<String>(SK.webDavUser) ?: "",
        webDavPassword = get<String>(SK.webDavPassword) ?: "",
    )
    return settingsEntity
}

fun SettingsEntity.toSettings(): Settings {
    return Settings(
        valueMap(
            fieldDescriptors = Settings.DESCRIPTORS,
            values = mapOf(
                SK.displayTheme to DisplayThemeEnum.fromValue(displayTheme),
                SK.clockColor to HsvColor.fromHex(clockColor),
                SK.spotColor to HsvColor.fromHex(spotColor),
                SK.language to Language.fromValue(language),
                SK.hideRead to BooleanEnum.fromValue(hideRead),
                SK.loadArticles to BooleanEnum.fromValue(loadArticles),
                SK.refreshInterval to RefreshIntervalEnum.fromValue(refreshInterval),
                SK.refreshWifiOnly to BooleanEnum.fromValue(refreshWifiOnly),
                SK.maxImageSize to lastMaxImageSize,
                SK.keepReadArticles to KeepArticlesEnum.fromValue(keepReadArticles),
                SK.keepUnreadArticles to KeepArticlesEnum.fromValue(keepUnreadArticles),
                SK.webDavUrl to webDavUrl,
                SK.webDavDirectory to webDavDirectory,
                SK.webDavUser to webDavUser,
                SK.webDavPassword to webDavPassword
            )
        )
    )
}


fun NewsFeedGroup.toNewsFeedGroupEntity(): NewsFeedGroupEntity {
    return NewsFeedGroupEntity(
        id = id,
        parentId = parentId,
        parentGroupName = parentGroupName,
        name = name,
        isKeywordBucket = isKeywordBucket,
        newsFeeds = newsFeeds,
        subGroups = subGroups
    )
}

fun NewsFeedGroupEntity.toNewsFeedGroup(): NewsFeedGroup {
    return NewsFeedGroup(
        id = id,
        parentId = parentId,
        parentGroupName = parentGroupName,
        name = name,
        isKeywordBucket = isKeywordBucket,
        newsFeeds = newsFeeds,
        subGroups = subGroups
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

fun GetAllNewsItemsWithArticles.toNewsItem(): NewsItem {
    return NewsItemEntity(
        id = id,
        feedName = feedName,
        identifier = identifier,
        publishedMillis = publishedMillis,
        publishedZone = publishedZone,
        updatedMillis = updatedMillis,
        updatedZone = updatedZone,
        lastSeenMillis = lastSeenMillis,
        link = link,
        title = title,
        summary = summary,
        content = content,
        keywords = keywords,
        image = image,
        imageTitle = imageTitle,
        imageCaption = imageCaption,
        isRead = isRead
    ).toNewsItem().copy(newsArticle = FullArticleEntity(
        id = id_?:0L,
        itemId = itemId?:0L,
        applicationJson = applicationJson?:listOf(),
        html = html?:"",
        imageItems = imageItems?:listOf(),
        videoItems = videoItems?:listOf(),
        audioItems = audioItems?:listOf(),
        articleImage = articleImage,
        discussionUrl = discussionUrl,
        commentCount = commentCount?:0L,
        isFree = isFree?:false,
        wordCount = wordCount?:0L,
        readingTime = readingTime?:0
    ).toFullArticle())
}

fun SearchNewsItems.toNewsItem(): NewsItem {
    return NewsItemEntity(
        id = id,
        feedName = feedName,
        identifier = identifier,
        publishedMillis = publishedMillis,
        publishedZone = publishedZone,
        updatedMillis = updatedMillis,
        updatedZone = updatedZone,
        lastSeenMillis = lastSeenMillis,
        link = link,
        title = title,
        summary = summary,
        content = content,
        keywords = keywords,
        image = image,
        imageTitle = imageTitle,
        imageCaption = imageCaption,
        isRead = isRead
    ).toNewsItem().copy(newsArticle = FullArticleEntity(
        id = id_?:0L,
        itemId = itemId?:0L,
        applicationJson = applicationJson?:listOf(),
        html = html?:"",
        imageItems = imageItems?:listOf(),
        videoItems = videoItems?:listOf(),
        audioItems = audioItems?:listOf(),
        articleImage = articleImage,
        discussionUrl = discussionUrl,
        commentCount = commentCount?:0L,
        isFree = isFree?:false,
        wordCount = wordCount?:0L,
        readingTime = readingTime?:0
    ).toFullArticle())
}

val stringListAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> = databaseValue.split(",")
    override fun encode(value: List<String>): String = value.joinToString(",")
}

val newsFeedsAdapter = object : ColumnAdapter<List<NewsFeedItem>, String> {
    override fun decode(databaseValue: String): List<NewsFeedItem> =
        if (databaseValue.isEmpty()) listOf() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<NewsFeedItem>): String =
        Json.encodeToString(value)
}

val subGroupsAdapter = object : ColumnAdapter<List<NewsFeedGroup>, String> {
    override fun decode(databaseValue: String): List<NewsFeedGroup> =
        if (databaseValue.isEmpty()) listOf() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<NewsFeedGroup>): String =
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
        id = id,
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
        id = id,
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
