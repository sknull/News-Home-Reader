package de.visualdigits.newshomereader.presentation.model

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
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
    val currentNewsFeedName: String? = null,
    val currentNewsItems: List<NewsItem> = listOf(),
    val visibleNewsItems: List<NewsItem> = listOf(),
    val allowClearVisibleNewsItems: Boolean = false,

    val filteredNewsItems: List<NewsItem> = listOf(),
    val newsItemSearchText: String? = null,
    val isNewsItemSearchActive: Boolean = false,

    val currentNewsItem: NewsItem? = null,
    val currentNewsArticle: FullArticle? = null,

    val settings: Settings? = null,

    val language: Language = Language.EN,

    val isShowInfos: Boolean = false,
    val isEditingSettings: Boolean = false,

    val newsFeedCatalog: NewsFeedCatalog? = null,
    val isViewingCatalog: Boolean = false,

    val catalogSearchText: String = "",
    val filteredCatalog: NewsFeedCatalog? = null,

    val isLoading: Boolean = false,

    val uiMessage: UiText? = null,
    val uiMessageSeverity: Severity? = null,

    val currentProgress: Float = 0.0f,
    val progressStage: ProgressStage = ProgressStage.NONE,

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

    override fun toString(): String {
        return buildString {
        append("NewsHomeReaderState(newsFeedGroups=")
        append(newsFeedGroups.joinToString(",") { e -> e.name })
        append(", previousNewsFeedGroup=")
        append(previousNewsFeedGroup?.name)
        append(", currentNewsFeedGroup=")
        append(currentNewsFeedGroup?.name)
        append(", currentNewsFeedItem=")
        append(currentNewsFeedItem?.name)
        append(", currentNewsFeedName=")
        append(currentNewsFeedName)
        append(", currentNewsItems=")
        append(currentNewsItems.joinToString(",") { e -> e.id.toString() })
        append(", visibleNewsItems=")
        append(visibleNewsItems.joinToString(",") { e -> e.id.toString() })
        append(", allowClearVisibleNewsItems=")
        append(allowClearVisibleNewsItems)
        append(", filteredNewsItems=")
        append(filteredNewsItems.joinToString(",") { e -> e.id.toString() })
        append(", newsItemSearchText=")
        append(newsItemSearchText)
        append(", isNewsItemSearchActive=")
        append(isNewsItemSearchActive)
        append(", currentNewsItem=")
        append(currentNewsItem?.id)
        append(", currentNewsArticle=")
        append(currentNewsArticle?.id)
        append(", language=")
        append(language.name)
        append(", isShowInfos=")
        append(isShowInfos)
        append(", isEditingSettings=")
        append(isEditingSettings)
        append(", isViewingCatalog=")
        append(isViewingCatalog)
        append(", catalogSearchText='")
        append(catalogSearchText)
        append("', filteredCatalog=")
        append(isLoading)
        append(", uiMessage=")
        append(uiMessage)
        append(", uiMessageSeverity=")
        append(uiMessageSeverity)
        append(", collapsibleState=")
        append(collapsibleState)
        append(", isEditMode=")
        append(isEditMode)
        append(", onlySubscribedFeeds=")
        append(onlySubscribedFeeds)
        append(", parentNewsFeedGroup=")
        append(parentNewsFeedGroup?.name)
        append(", isEditingNewsFeedGroup=")
        append(isEditingNewsFeedGroup)
        append(", isAddingNewsFeedGroup=")
        append(isAddingNewsFeedGroup)
        append(", isDeletingNewsFeedGroup=")
        append(isDeletingNewsFeedGroup)
        append(", currentNewsFeedGroupToDelete=")
        append(currentNewsFeedGroupToDelete?.name)
        append(", originalNewsFeedGroup=")
        append(originalNewsFeedGroup?.name)
        append(", editedNewsFeedGroup=")
        append(editedNewsFeedGroup?.name)
        append(", isEditingNewsFeedConfiguration=")
        append(isEditingNewsFeedConfiguration)
        append(", isAddingNewsFeedConfiguration=")
        append(isAddingNewsFeedConfiguration)
        append(", isDeletingNewsFeedConfiguration=")
        append(isDeletingNewsFeedConfiguration)
        append(", deleteNewsFeedItem=")
        append(deleteNewsFeedItem?.name)
        append(")")
    }
    }
}
