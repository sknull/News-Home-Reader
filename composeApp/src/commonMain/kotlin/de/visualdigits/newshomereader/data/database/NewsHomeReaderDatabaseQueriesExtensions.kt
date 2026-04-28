package de.visualdigits.newshomereader.data.database

import de.visualdigits.newshomereader.FullArticleEntity
import de.visualdigits.newshomereader.NewsFeedEntity
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.NewsItemEntity
import de.visualdigits.newshomereader.SettingsEntity
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup

fun NewsHomeReaderDatabaseQueries.getAllNewsFeedGroups(): List<NewsFeedGroup> {
    val childrenByParent = getAllNewsFeedGroupEntities()
        .executeAsList()
        .groupBy { it.parentId }
    return try {
        childrenByParent[null]
            ?.map { rootEntity ->
                buildNodeRecursive(rootEntity, childrenByParent)
            } ?: emptyList()
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN, e)
        listOf()
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
        name = currentEntity.name,
        newsFeeds = currentEntity.newsFeeds,
        subGroups = subGroups
    )
}

fun NewsHomeReaderDatabaseQueries.upsertNewsFeedGroup(newsFeedGroupEntity: NewsFeedGroupEntity) {
    insertNewsFeedGroupEntity(
        parentId = newsFeedGroupEntity.parentId,
        parentGroupName = newsFeedGroupEntity.parentGroupName,
        name = newsFeedGroupEntity.name,
        newsFeeds = newsFeedGroupEntity.newsFeeds,
        subGroups = newsFeedGroupEntity.subGroups
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

fun NewsHomeReaderDatabaseQueries.upsertNewsFeed(newsFeedEntity: NewsFeedEntity): Boolean {
    val existingNewsFeedEntity = getNewsFeedByFeedName(newsFeedEntity.feedName).executeAsOneOrNull()
    return if (existingNewsFeedEntity != null) {
        updateNewsFeed(newsFeedEntity)
        !existingNewsFeedEntity.isEqualTo(newsFeedEntity)
    } else {
        insertNewsFeed(newsFeedEntity)
        true
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

fun NewsHomeReaderDatabaseQueries.upsertNewsItem(newsItemEntity: NewsItemEntity, forceUpdate: Boolean = false): Pair<NewsItemEntity, Boolean> {
    val cleanFeed = newsItemEntity.feedName.trim().lowercase()
    val cleanIdentifier = newsItemEntity.identifier.trim().lowercase()

    val normalizedItem = newsItemEntity.copy(
        feedName = cleanFeed,
        identifier = cleanIdentifier
    )

    val existingNewsItemEntity = getNewsItemByFeedNameAndIdentifier(cleanFeed, cleanIdentifier).executeAsOneOrNull()
    return if (existingNewsItemEntity != null) {
        if (forceUpdate || normalizedItem.updatedMillis > existingNewsItemEntity.updatedMillis) {
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


fun NewsHomeReaderDatabaseQueries.upsertFullArticle(fullArticleEntity: FullArticleEntity): Boolean {
    val existingFullArticleEntity = getFullArticleByItemId(fullArticleEntity.itemId).executeAsOneOrNull()
    return if (existingFullArticleEntity != null) {
        insertFullArticle(fullArticleEntity)
        !existingFullArticleEntity.isEqualto(fullArticleEntity)
    } else {
        insertFullArticle(fullArticleEntity)
        true
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
        readingTime = fullArticleEntity.readingTime,
    )
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
    imageItems == other.imageItems &&
    videoItems == other.videoItems &&
    audioItems == other.audioItems &&
    articleImage == other.articleImage &&
    discussionUrl == other.discussionUrl &&
    commentCount == other.commentCount &&
    isFree == other.isFree &&
    wordCount == other.wordCount &&
    readingTime == other.readingTime

}
