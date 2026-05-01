package de.visualdigits.newshomereader.presentation.model

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.common.domain.model.errorhandling.LogMessage
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import org.jetbrains.compose.resources.StringResource

@Stable
data class NewsHomeReaderState(

    val maxImageSize: Int? = null,

    val newsFeedGroups: List<NewsFeedGroup> = listOf(),

    val currentNewsFeedGroup: NewsFeedGroup? = null,

    val currentNewsFeedItem: NewsFeedItem? = null,
    val currentNewsFeedName: String? = null,
    val currentNewsItems: List<NewsItem> = listOf(),
    val visibleNewsItems: List<NewsItem> = listOf(),

    val currentNewsItem: NewsItem? = null,
    val currentNewsArticle: FullArticle? = null,

    val settings: Settings? = null,
    val originalSettings: Settings? = null,

    val selectedTabIndex: Int = 0,
    val selectedTabLabel: StringResource? = null,
    val tabLabels: List<StringResource> = listOf(),

    val language: Language = Language.EN,

    val isShowInfos: Boolean = false,
    val isEditingSettings: Boolean = false,

    val newsFeedCatalog: NewsFeedCatalog? = null,
    val isViewingCatalog: Boolean = false,

    val searchText: String = "",
    val isSearchActive: Boolean = false,
    val filteredCatalog: NewsFeedCatalog? = null,

    val isLoading: Boolean = false,
    val isConverting: Boolean = false,

    val uiMessage: UiText? = null,
    val uiMessageSeverity: Severity? = null,

    val currentProgress: Float = 0.0f,
    val progressStage: ProgressStage = ProgressStage.NONE,
    val logs: List<LogMessage> = listOf(),

    val collapsibleState: Map<String, Boolean> = mapOf(),

    val isEditMode: Boolean = false,
    val onlySubscribedFeeds: Boolean = false,

    val parentNewsFeedGroupName: String? = null,
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

    val lookupNewsFeedGroupMap
        get() = newsFeedGroups.flatMap { mainGroup ->
        mainGroup.subGroups.flatMap { subGroup ->
            subGroup.newsFeeds.map { it.name?.trim()?.lowercase() to subGroup }
        } + mainGroup.newsFeeds.map { it.name?.trim()?.lowercase() to mainGroup }
    }.toMap()

    val lookupNewsFeedMap
        get() = newsFeedGroups.flatMap { mainGroup ->
        mainGroup.subGroups.flatMap { subGroup ->
            subGroup.newsFeeds.map { it.name?.trim()?.lowercase() to it }
        } + mainGroup.newsFeeds.map { it.name?.trim()?.lowercase() to it }
    }.toMap()

}
