package de.visualdigits.newshomereader.data.database

import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.NewsItemEntity
import de.visualdigits.newshomereader.SettingsEntity

fun NewsHomeReaderDatabaseQueries.upsertNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity) {
    val entity = getNewsFeedGroupEntityById(newsFeedGroupEntity.id).executeAsOneOrNull()
    if (entity != null) {
        updateNewsFeedGroup(newsFeedGroupEntity)
    } else {
        insertNewsFeedGroup(newsFeedGroupEntity)
    }
}

fun NewsHomeReaderDatabaseQueries.updateNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity) {
    updateNewsFeedGroupEntity(
        name = newsFeedGroupEntity.name,
        newsFeeds = newsFeedGroupEntity.newsFeeds,
        id = newsFeedGroupEntity.id
    )
}

fun NewsHomeReaderDatabaseQueries.insertNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity) {
    insertNewsFeedGroupEntity(
        name = newsFeedGroupEntity.name,
        newsFeeds = newsFeedGroupEntity.newsFeeds
    )
}

fun NewsHomeReaderDatabaseQueries.upsertSettings(settingsEntity: SettingsEntity) {
    val entity = getSettingsById(settingsEntity.id).executeAsOneOrNull()
    if (entity != null) {
        updateSettings(settingsEntity)
    } else {
        insertSettings(settingsEntity)
    }
}

fun NewsHomeReaderDatabaseQueries.insertSettings(settingsEntity: SettingsEntity) {
    insertSettings(
        displayTheme = settingsEntity.displayTheme,
        language = settingsEntity.language,
        hideRead = settingsEntity.hideRead,
        loadArticles = settingsEntity.loadArticles,
        refreshInterval = settingsEntity.refreshInterval,
        refreshWifiOnly = settingsEntity.refreshWifiOnly,
        lastMaxImageSize = settingsEntity.lastMaxImageSize,
        keepReadArticles = settingsEntity.keepReadArticles,
        keepUnreadArticles = settingsEntity.keepUnreadArticles
    )
}

fun NewsHomeReaderDatabaseQueries.updateSettings(settingsEntity: SettingsEntity) {
    updateSettingsEntity(
        displayTheme = settingsEntity.displayTheme,
        language = settingsEntity.language,
        hideRead = settingsEntity.hideRead,
        loadArticles = settingsEntity.loadArticles,
        refreshInterval = settingsEntity.refreshInterval,
        refreshWifiOnly = settingsEntity.refreshWifiOnly,
        lastMaxImageSize = settingsEntity.lastMaxImageSize,
        keepReadArticles = settingsEntity.keepReadArticles,
        keepUnreadArticles = settingsEntity.keepUnreadArticles,
        id = settingsEntity.id
    )
}

fun NewsHomeReaderDatabaseQueries.upsertNewsFeed(newsFeedEntity: NewsFeedEntity) {
    val entity = getNewsFeedByFeedName(newsFeedEntity.feedName).executeAsOneOrNull()
    if (entity != null) {
        updateNewsFeed(newsFeedEntity)
    } else {
        insertNewsFeed(newsFeedEntity)
    }
}

fun NewsHomeReaderDatabaseQueries.insertNewsFeed(newsFeedEntity: NewsFeedEntity) {
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
}

fun NewsHomeReaderDatabaseQueries.updateNewsFeed(newsFeedEntity: NewsFeedEntity) {
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
}

fun NewsHomeReaderDatabaseQueries.upsertNewsItem(newsItemEntity: NewsItemEntity, forceUpdate: Boolean = false): NewsItemEntity {
    val cleanFeed = newsItemEntity.feedName.trim().lowercase()
    val cleanIdentifier = newsItemEntity.identifier.trim().lowercase()

    val normalizedItem = newsItemEntity.copy(
        feedName = cleanFeed,
        identifier = cleanIdentifier
    )

    return transactionWithResult {
        val existing = getNewsItemByFeedNameAndIdentifier(cleanFeed, cleanIdentifier).executeAsOneOrNull()
        if (existing != null) {
            if (forceUpdate || normalizedItem.updatedMillis > existing.updatedMillis) {
                val toUpdate = normalizedItem.copy(id = existing.id)
                updateNewsItem(toUpdate)
                toUpdate
            } else {
                existing
            }
        } else {
            insertNewsItem(normalizedItem)
            val inserted = getNewsItemByFeedNameAndIdentifier(cleanFeed, cleanIdentifier).executeAsOne()
            inserted
        }
    }
}

fun NewsHomeReaderDatabaseQueries.insertNewsItem(newsItemEntity: NewsItemEntity) {
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
}

fun NewsHomeReaderDatabaseQueries.updateNewsItem(newsItemEntity: NewsItemEntity) {
    updateNewsItem(
        identifier = newsItemEntity.identifier,
        feedName = newsItemEntity.feedName,
        publishedMillis = newsItemEntity.publishedMillis,
        publishedZone = newsItemEntity.publishedZone,
        updatedMillis = newsItemEntity.updatedMillis,
        updatedZone = newsItemEntity.updatedZone,
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
}


fun NewsHomeReaderDatabaseQueries.upsertFullArticle(fullArticleEntity: FullArticleEntity) {
    val entity = getFullArticleById(fullArticleEntity.itemId).executeAsOneOrNull()
    if (entity != null) {
        updateFullArticle(fullArticleEntity)
    } else {
        insertFullArticle(fullArticleEntity)
    }
}

fun NewsHomeReaderDatabaseQueries.insertFullArticle(fullArticleEntity: FullArticleEntity) {
    insertFullArticle(
        itemId = fullArticleEntity.itemId,
        applicationJson = fullArticleEntity.applicationJson,
        html = fullArticleEntity.html,
        imageItems = fullArticleEntity.imageItems,
        videoItems = fullArticleEntity.videoItems,
        audioItems = fullArticleEntity.audioItems,
        articleImage = fullArticleEntity.articleImage,
        discussionUrl = fullArticleEntity.discussionUrl,
        commentCount = fullArticleEntity.commentCount,
        isFree = fullArticleEntity.isFree,
        wordCount = fullArticleEntity.wordCount,
        readingTime = fullArticleEntity.readingTime
    )
}

fun NewsHomeReaderDatabaseQueries.updateFullArticle(fullArticleEntity: FullArticleEntity) {
    updateFullArticle(
        applicationJson = fullArticleEntity.applicationJson,
        html = fullArticleEntity.html,
        imageItems = fullArticleEntity.imageItems,
        videoItems = fullArticleEntity.videoItems,
        audioItems = fullArticleEntity.audioItems,
        articleImage = fullArticleEntity.articleImage,
        discussionUrl = fullArticleEntity.discussionUrl,
        commentCount = fullArticleEntity.commentCount,
        isFree = fullArticleEntity.isFree,
        wordCount = fullArticleEntity.wordCount,
        readingTime = fullArticleEntity.readingTime,
        itemId = fullArticleEntity.itemId,
    )
}
