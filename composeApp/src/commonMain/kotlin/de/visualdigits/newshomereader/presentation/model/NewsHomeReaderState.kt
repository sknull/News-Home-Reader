package de.visualdigits.newshomereader.presentation.model

import androidx.compose.runtime.Stable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.newshomereader.domain.model.errorhandling.LogMessage
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import org.jetbrains.compose.resources.StringResource

@Stable
data class NewsHomeReaderState(

    val maxImageSize: Int? = null,

    val newsFeedGroups: List<NewsFeedGroup> = listOf(),

    val currentFeedConfiguration: NewsFeedConfiguration? = null,
    val currentFeedName: String? = null,
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

    val isLoading: Boolean = false,
    val isConverting: Boolean = false,

    val uiMessage: UiText? = null,
    val uiMessageSeverity: Severity? = null,

    val currentProgress: Float = 0.0f,
    val logs: List<LogMessage> = listOf(),

    val collapsibleState: Map<String, Boolean> = mapOf()
)
