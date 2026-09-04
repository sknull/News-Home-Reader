package de.visualdigits.newshomereader.data.database

import co.touchlab.kermit.Logger
import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.NewsItemEntity
import de.visualdigits.newshomereader.SettingsEntity
import de.visualdigits.newshomereader.domain.model.opml.OutlineType
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup

fun NewsHomeReaderDatabaseQueries.getAllNewsFeedGroups(): List<NewsFeedGroup> {
    val executeAsList = getAllNewsFeedGroupEntities()
        .executeAsList()
    val childrenByParent = executeAsList
        .groupBy { it.parentId }
    return try {
        val groups = childrenByParent[null]
            ?.map { rootEntity ->
                buildNodeRecursive(rootEntity, childrenByParent)
            } ?: emptyList()
        groups
    } catch (e: Exception) {
        Logger.e("Could not get news feed groups", e)
        listOf()
    }
}

fun NewsHomeReaderDatabaseQueries.upsertNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity): NewsFeedGroupEntity {
    val existingNewsFeedEntity = getNewsFeedGroupEntityById(newsFeedGroupEntity.id).executeAsOneOrNull()
    return try {
        if (existingNewsFeedEntity != null) {
            updateNewsFeedGroup(newsFeedGroupEntity)
        } else {
            insertNewsFeedGroup(newsFeedGroupEntity)
        }
    } catch (e: Exception) {
        Logger.e("Could not upsert news feed group", e)
        newsFeedGroupEntity
    }
}

fun NewsHomeReaderDatabaseQueries.upsertNewsFeedGroupByName(newsFeedGroupEntity: NewsFeedGroupEntity): NewsFeedGroupEntity {
    val existingNewsFeedEntity = getNewsFeedGroupEntityByName(
        name = newsFeedGroupEntity.name,
        parentGroupName = newsFeedGroupEntity.parentGroupName
    ).executeAsOneOrNull()
    return try {
        if (existingNewsFeedEntity != null) {
            updateNewsFeedGroup(newsFeedGroupEntity)
        } else {
            insertNewsFeedGroup(newsFeedGroupEntity)
        }
    } catch (e: Exception) {
        Logger.e("Could not upsert news feed group", e)
        newsFeedGroupEntity
    }
}

fun NewsHomeReaderDatabaseQueries.insertNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity): NewsFeedGroupEntity {
    return try {
        insertNewsFeedGroupEntity(
            parentId = newsFeedGroupEntity.parentId,
            parentGroupName = newsFeedGroupEntity.parentGroupName,
            name = newsFeedGroupEntity.name,
            type = newsFeedGroupEntity.type,
            newsFeeds = newsFeedGroupEntity.newsFeeds
        )

        getNewsFeedGroupEntityByName(name = newsFeedGroupEntity.name, parentGroupName = newsFeedGroupEntity.parentGroupName).executeAsOne()
    } catch (e: Exception) {
        Logger.e("Could not insert news feed group", e)
        newsFeedGroupEntity
    }
}

fun NewsHomeReaderDatabaseQueries.updateNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity): NewsFeedGroupEntity {
    return try {
        updateNewsFeedGroupEntity(
            parentId = newsFeedGroupEntity.parentId,
            parentGroupName = newsFeedGroupEntity.parentGroupName,
            name = newsFeedGroupEntity.name,
            type = newsFeedGroupEntity.type,
            newsFeeds = newsFeedGroupEntity.newsFeeds,
            id = newsFeedGroupEntity.id
        )
        getNewsFeedGroupEntityByName(name = newsFeedGroupEntity.name, parentGroupName = newsFeedGroupEntity.parentGroupName).executeAsOne()
    } catch (e: Exception) {
        Logger.e("Could not update news feed group", e)
        newsFeedGroupEntity
    }

}

fun NewsHomeReaderDatabaseQueries.upsertSettings(settingsEntity: SettingsEntity) {
    val entity = getSettingsById(settingsEntity.id).executeAsOneOrNull()
    try {
        if (entity != null) {
            updateSettings(settingsEntity)
        } else {
            insertSettings(settingsEntity)
        }
    } catch (e: Exception) {
        Logger.e("Could not upsert settings", e)
    }
}

fun NewsHomeReaderDatabaseQueries.insertSettings(settingsEntity: SettingsEntity) {
    try {
        insertSettings(
            backgroundColor = settingsEntity.backgroundColor,
            buttonColor = settingsEntity.buttonColor,
            textColor = settingsEntity.textColor,
            spotColor = settingsEntity.spotColor,
            clockColor = settingsEntity.clockColor,
            language = settingsEntity.language,
            hideRead = settingsEntity.hideRead,
            loadArticles = settingsEntity.loadArticles,
            prefetchImages = settingsEntity.prefetchImages,
            refreshInterval = settingsEntity.refreshInterval,
            refreshWifiOnly = settingsEntity.refreshWifiOnly,
            lastMaxImageSize = settingsEntity.lastMaxImageSize,
            keepReadArticles = settingsEntity.keepReadArticles,
            keepUnreadArticles = settingsEntity.keepUnreadArticles,
            webDavUrl = settingsEntity.webDavUrl,
            webDavDirectory = settingsEntity.webDavDirectory,
            webDavUser = settingsEntity.webDavUser,
            webDavPassword = settingsEntity.webDavPassword,
        )
    } catch (e: Exception) {
        Logger.e("Could not insert settings", e)
    }
}

fun NewsHomeReaderDatabaseQueries.updateSettings(settingsEntity: SettingsEntity) {
    try {
        updateSettingsEntity(
            backgroundColor = settingsEntity.backgroundColor,
            buttonColor = settingsEntity.buttonColor,
            textColor = settingsEntity.textColor,
            spotColor = settingsEntity.spotColor,
            clockColor = settingsEntity.clockColor,
            language = settingsEntity.language,
            hideRead = settingsEntity.hideRead,
            loadArticles = settingsEntity.loadArticles,
            prefetchImages = settingsEntity.prefetchImages,
            refreshInterval = settingsEntity.refreshInterval,
            refreshWifiOnly = settingsEntity.refreshWifiOnly,
            lastMaxImageSize = settingsEntity.lastMaxImageSize,
            keepReadArticles = settingsEntity.keepReadArticles,
            keepUnreadArticles = settingsEntity.keepUnreadArticles,
            webDavUrl = settingsEntity.webDavUrl,
            webDavDirectory = settingsEntity.webDavDirectory,
            webDavUser = settingsEntity.webDavUser,
            webDavPassword = settingsEntity.webDavPassword,
            id = settingsEntity.id
        )
    } catch (e: Exception) {
        Logger.e("Could not update settings", e)
    }
}

fun NewsHomeReaderDatabaseQueries.upsertNewsFeed(newsFeedEntity: NewsFeedEntity): Boolean {
    val existingNewsFeedEntity = getNewsFeedByFeedName(newsFeedEntity.feedName).executeAsOneOrNull()
    return try {
        if (existingNewsFeedEntity != null) {
            updateNewsFeed(newsFeedEntity)
            !existingNewsFeedEntity.isEqualTo(newsFeedEntity)
        } else {
            insertNewsFeed(newsFeedEntity)
            true
        }
    } catch (e: Exception) {
        Logger.e("Could not upsert news feed", e)
        false
    }
}

fun NewsHomeReaderDatabaseQueries.insertNewsFeed(newsFeedEntity: NewsFeedEntity) {
    try {
        insertNewsFeed(
            identifier = newsFeedEntity.identifier,
            title = newsFeedEntity.title,
            description = newsFeedEntity.description,
            link = newsFeedEntity.link,
            image = newsFeedEntity.image,
            imageTitle = newsFeedEntity.imageTitle,
            imageCaption = newsFeedEntity.imageCaption,
            updatedMillis = newsFeedEntity.updatedMillis,
            updatedZone = newsFeedEntity.updatedZone,
            rights = newsFeedEntity.rights,
            language = newsFeedEntity.language,
            keywords = newsFeedEntity.keywords,
            feedName = newsFeedEntity.feedName
        )
    } catch (e: Exception) {
        Logger.e("Could not inset news feed", e)
    }
}

fun NewsHomeReaderDatabaseQueries.updateNewsFeed(newsFeedEntity: NewsFeedEntity) {
    try {
        updateNewsFeed(
            identifier = newsFeedEntity.identifier,
            title = newsFeedEntity.title,
            description = newsFeedEntity.description,
            link = newsFeedEntity.link,
            image = newsFeedEntity.image,
            imageTitle = newsFeedEntity.imageTitle,
            imageCaption = newsFeedEntity.imageCaption,
            updatedMillis = newsFeedEntity.updatedMillis,
            updatedZone = newsFeedEntity.updatedZone,
            rights = newsFeedEntity.rights,
            language = newsFeedEntity.language,
            keywords = newsFeedEntity.keywords,
            feedName = newsFeedEntity.feedName
        )
    } catch (e: Exception) {
        Logger.e("Could not update news feed", e)
    }
}

fun NewsHomeReaderDatabaseQueries.upsertNewsItem(newsItemEntity: NewsItemEntity, forceUpdate: Boolean = false): Pair<NewsItemEntity, Boolean> {
    val cleanFeed = newsItemEntity.feedName.trim().lowercase()
    val cleanIdentifier = newsItemEntity.identifier.trim().lowercase()

    val normalizedItem = newsItemEntity.copy(
        feedName = cleanFeed,
        identifier = cleanIdentifier
    )

    val existingNewsItemEntity = getNewsItemByFeedNameAndIdentifier(cleanFeed, cleanIdentifier).executeAsOneOrNull()
    return try {
        if (existingNewsItemEntity != null) {
            if (forceUpdate || normalizedItem.publishedMillis > existingNewsItemEntity.publishedMillis) {
                val toUpdate = normalizedItem.copy(id = existingNewsItemEntity.id)
                updateNewsItem(toUpdate)
                Pair(toUpdate, existingNewsItemEntity.isEqualTo(newsItemEntity))
            } else {
                Pair(existingNewsItemEntity, false)
            }
        } else {
            insertNewsItem(normalizedItem)
            val inserted = getNewsItemByFeedNameAndIdentifier(cleanFeed, cleanIdentifier).executeAsOne()
            Pair(inserted, true)
        }
    } catch (e: Exception) {
        Logger.e("Could not upsert news item", e)
        Pair(newsItemEntity, false)
    }
}

fun NewsHomeReaderDatabaseQueries.insertNewsItem(newsItemEntity: NewsItemEntity) {
    try {
        insertNewsItem(
            identifier = newsItemEntity.identifier,
            feedName = newsItemEntity.feedName,
            publishedMillis = newsItemEntity.publishedMillis,
            publishedZone = newsItemEntity.publishedZone,
            updatedMillis = newsItemEntity.updatedMillis,
            lastSeenMillis = newsItemEntity.lastSeenMillis,
            updatedZone = newsItemEntity.updatedZone,
            link = newsItemEntity.link,
            title = newsItemEntity.title,
            summary = newsItemEntity.summary,
            content = newsItemEntity.content,
            keywords = newsItemEntity.keywords,
            image = newsItemEntity.image,
            imageTitle = newsItemEntity.imageTitle,
            imageCaption = newsItemEntity.imageCaption,
            isRead = newsItemEntity.isRead
        )
    } catch (e: Exception) {
        Logger.e("Could not insert news item", e)
    }
}

fun NewsHomeReaderDatabaseQueries.updateNewsItem(newsItemEntity: NewsItemEntity) {
    try {
        updateNewsItem(
            identifier = newsItemEntity.identifier,
            feedName = newsItemEntity.feedName,
            publishedMillis = newsItemEntity.publishedMillis,
            publishedZone = newsItemEntity.publishedZone,
            updatedMillis = newsItemEntity.updatedMillis,
            updatedZone = newsItemEntity.updatedZone,
            lastSeenMillis = newsItemEntity.lastSeenMillis,
            link = newsItemEntity.link,
            title = newsItemEntity.title,
            summary = newsItemEntity.summary,
            content = newsItemEntity.content,
            keywords = newsItemEntity.keywords,
            image = newsItemEntity.image,
            imageTitle = newsItemEntity.imageTitle,
            imageCaption = newsItemEntity.imageCaption,
            isRead = newsItemEntity.isRead,
            id = newsItemEntity.id
        )
    } catch (e: Exception) {
        Logger.e("Could not update news item", e)
    }
}


fun NewsHomeReaderDatabaseQueries.upsertFullArticle(fullArticleEntity: FullArticleEntity): Boolean {
    val existingFullArticleEntity = getFullArticleByItemId(fullArticleEntity.itemId).executeAsOneOrNull()
    return try {
        if (existingFullArticleEntity != null) {
            updateFullArticle(fullArticleEntity)
            !existingFullArticleEntity.isEqualto(fullArticleEntity)
        } else {
            insertFullArticle(fullArticleEntity)
            true
        }
    } catch (e: Exception) {
        Logger.e("Could not upsert full article", e)
        false
    }
}

fun NewsHomeReaderDatabaseQueries.insertFullArticle(fullArticleEntity: FullArticleEntity) {
    try {
        insertFullArticle(
            itemId = fullArticleEntity.itemId,
            applicationJson = fullArticleEntity.applicationJson,
            html = fullArticleEntity.html,
            parts = fullArticleEntity.parts,
            imageItems = fullArticleEntity.imageItems,
            videoItems = fullArticleEntity.videoItems,
            audioItems = fullArticleEntity.audioItems,
            articleImage = fullArticleEntity.articleImage,
            discussionUrl = fullArticleEntity.discussionUrl,
            commentCount = fullArticleEntity.commentCount,
            isPaid = fullArticleEntity.isPaid,
            wordCount = fullArticleEntity.wordCount,
            readingTime = fullArticleEntity.readingTime,
        )
    } catch (e: Exception) {
        Logger.e("Could not insert full article", e)
    }
}

fun NewsHomeReaderDatabaseQueries.updateFullArticle(fullArticleEntity: FullArticleEntity) {
    try {
        updateFullArticle(
            itemId = fullArticleEntity.itemId,
            applicationJson = fullArticleEntity.applicationJson,
            html = fullArticleEntity.html,
            parts = fullArticleEntity.parts,
            imageItems = fullArticleEntity.imageItems,
            videoItems = fullArticleEntity.videoItems,
            audioItems = fullArticleEntity.audioItems,
            articleImage = fullArticleEntity.articleImage,
            discussionUrl = fullArticleEntity.discussionUrl,
            commentCount = fullArticleEntity.commentCount,
            isPaid = fullArticleEntity.isPaid,
            wordCount = fullArticleEntity.wordCount,
            readingTime = fullArticleEntity.readingTime,
            id = fullArticleEntity.id
        )
    } catch (e: Exception) {
        Logger.e("Could not update full article", e)
    }
}

private fun buildNodeRecursive(
    currentEntity: NewsFeedGroupEntity,
    childrenByParent: Map<Long?, List<NewsFeedGroupEntity>>
): NewsFeedGroup {
    val subGroups = childrenByParent[currentEntity.id]?.map { childEntity ->
        buildNodeRecursive(childEntity, childrenByParent)
    } ?: emptyList()

    return NewsFeedGroup(
        id = currentEntity.id,
        parentId = currentEntity.parentId,
        parentGroupName = currentEntity.parentGroupName,
        name = currentEntity.name,
        outlineType = OutlineType.valueOf(currentEntity.type),
        newsFeeds = currentEntity.newsFeeds,
        subGroups = subGroups
    )
}

fun NewsFeedGroupEntity.isEqualTo(other: NewsFeedGroupEntity): Boolean {
    return   id == other.id &&
    parentId == other.parentId &&
    parentGroupName == other.parentGroupName &&
    name == other.name &&
    newsFeeds == other.newsFeeds
}

fun NewsFeedEntity.isEqualTo(other: NewsFeedEntity): Boolean {
    return id ==  other.id &&
    identifier ==  other.identifier &&
    feedName ==  other.feedName &&
    title ==  other.title &&
    description ==  other.description &&
    link ==  other.link &&
    image ==  other.image &&
    imageTitle ==  other.imageTitle &&
    imageCaption ==  other.imageCaption &&
    updatedMillis ==  other.updatedMillis &&
    updatedZone ==  other.updatedZone &&
    rights ==  other.rights &&
    language ==  other.language &&
    keywords ==  other.keywords
}

fun NewsItemEntity.isEqualTo(other: NewsItemEntity): Boolean {
    return id == other.id &&
    feedName == other.feedName &&
    identifier  == other.identifier  &&
    publishedMillis == other.publishedMillis &&
    publishedZone  == other.publishedZone  &&
    updatedMillis == other.updatedMillis &&
    lastSeenMillis == other.lastSeenMillis &&
    updatedZone  == other.updatedZone  &&
    link  == other.link  &&
    title  == other.title  &&
    summary  == other.summary  &&
    content == other.content &&
    keywords  == other.keywords  &&
    image  == other.image  &&
    imageTitle  == other.imageTitle  &&
    imageCaption  == other.imageCaption  &&
    isRead == other.isRead
}

fun FullArticleEntity.isEqualto(other: FullArticleEntity): Boolean {
    return id == other.id &&
    itemId == other.itemId &&
    applicationJson == other.applicationJson &&
    html  == other.html  &&
    parts  == other.parts  &&
    imageItems == other.imageItems &&
    videoItems == other.videoItems &&
    audioItems == other.audioItems &&
    articleImage == other.articleImage &&
    discussionUrl == other.discussionUrl &&
    commentCount == other.commentCount &&
    isPaid == other.isPaid &&
    wordCount == other.wordCount &&
    readingTime == other.readingTime

}
