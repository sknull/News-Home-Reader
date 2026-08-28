package de.visualdigits.newshomereader.presentation.model

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.opml.OutlineType
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem

@Stable
data class NewsHomeReaderState(

    val maxImageSize: Int? = null,

    val newsFeedGroups: List<NewsFeedGroup> = listOf(),

    val previousNewsFeedGroup: NewsFeedGroup? = null,
    val currentNewsFeedGroup: NewsFeedGroup? = null,

    val currentNewsFeedItem: NewsFeedItem? = null,
    val previousOutlineType: OutlineType? = null,
    val previousNewsFeedName: String? = null,
    val currentNewsFeedName: String? = null,

    val currentKeywordBucket: String? = null,
    val newsItemSearchText: String? = null,
    val isNewsItemSearchActive: Boolean = false,

    val currentNewsItem: NewsItem? = null,

    val language: Language = Language.EN,

    val isShowInfos: Boolean = false,
    val isEditingSettings: Boolean = false,

    val newsFeedCatalog: NewsFeedCatalog? = null,
    val isViewingCatalog: Boolean = false,

    val catalogSearchText: String = "",
    val filteredCatalog: NewsFeedCatalog? = null,

    val uiMessage: UiText? = null,
    val uiMessageSeverity: Severity? = null,

    val collapsibleState: Map<String, Boolean> = mapOf(),

    val isEditMode: Boolean = false,
    val onlySubscribedFeeds: Boolean = false,

    val parentNewsFeedGroup: NewsFeedGroup? = null,
    val originalNewsFeedConfiguration: NewsFeedConfiguration? = null,
    val editedNewsFeedConfiguration: NewsFeedConfiguration? = null,
    val isEditingNewsFeedGroup: Boolean = false,
    val isAddingNewsFeedGroup: Boolean = false,
    val isDeletingNewsFeedGroup: Boolean = false,

    val currentNewsFeedGroupToDelete: NewsFeedGroup? = null,
    val originalNewsFeedGroup: NewsFeedGroup? = null,
    val editedNewsFeedGroup: NewsFeedGroup? = null,

    val isEditingNewsFeedConfiguration: Boolean = false,
    val isAddingNewsFeedConfiguration: Boolean = false,
    val isDeletingNewsFeedConfiguration: Boolean = false,

    val deleteNewsFeedItem: NewsFeedItem? = null,
) {

    val lookupNewsFeedMap
        get() = newsFeedGroups.flatMap { mainGroup ->
        mainGroup.subGroups.flatMap { subGroup ->
            subGroup.newsFeeds.map { it.name?.trim()?.lowercase() to it }
        } + mainGroup.newsFeeds.map { it.name?.trim()?.lowercase() to it }
    }.toMap()
}
