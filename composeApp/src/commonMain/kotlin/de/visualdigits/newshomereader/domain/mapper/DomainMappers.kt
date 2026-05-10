package de.visualdigits.newshomereader.domain.mapper

import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogCategory
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogItem
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NC
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem


fun NewsFeedConfiguration.toNewsFeedItem(): NewsFeedItem {
    val newsFeedItem = NewsFeedItem(
        name = get<String>(NC.feedName),
        mainGroupName = get<String>(NC.mainGroupName) ?: error("No main group given"),
        subGroupName = get<String>(NC.subGroupName),
        imageUrl = get<String>(NC.imageUrl),
        url = get<String>(NC.url),
        stopWords = get<List<String>>(NC.stopWords)
    )
    return newsFeedItem
}

fun NewsFeedItem.toNewsFeedConfiguration(newsFeedGroups: List<NewsFeedGroup>): NewsFeedConfiguration {
    val newsFeedConfiguration = NewsFeedConfiguration(newsFeedGroups = newsFeedGroups)
    newsFeedConfiguration.set(NC.feedName, name)
    newsFeedConfiguration.set(NC.mainGroupName, mainGroupName)
    newsFeedConfiguration.set(NC.subGroupName, subGroupName)
    newsFeedConfiguration.set(NC.imageUrl, imageUrl)
    newsFeedConfiguration.set(NC.url, url)
    newsFeedConfiguration.set(NC.stopWords, stopWords)

    return newsFeedConfiguration
}

fun NewsFeedCatalogItem.toNewsFeedItem(): NewsFeedItem {
    return if (parentCategory?.parentCategory != null) {
        NewsFeedItem(
            name = name,
            mainGroupName = parentCategory?.parentCategory?.name?:error("No root category given"),
            subGroupName = parentCategory?.name,
            url = url
        )
    } else {
        NewsFeedItem(
            name = name,
            mainGroupName = parentCategory?.name?:error("No parent category given"),
            subGroupName = null,
            url = url
        )
    }
}

fun NewsFeedCatalogCategory.toNewsFeedGroup(): NewsFeedGroup {
    return NewsFeedGroup(
        id = 0L,
        parentId = 0L,
        parentGroupName = parentCategory?.name,
        name = name,
        newsFeeds = feeds.map { f -> f.toNewsFeedItem() },
        subGroups = subCategories.map { sc -> sc.toNewsFeedGroup() }
    )
}
