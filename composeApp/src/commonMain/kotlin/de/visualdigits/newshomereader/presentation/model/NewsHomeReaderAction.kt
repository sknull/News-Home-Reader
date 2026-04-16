package de.visualdigits.newshomereader.presentation.model

import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream
import org.jetbrains.compose.resources.StringResource

sealed interface NewsHomeReaderAction {

    //
    // Settings
    //

    data class OnEditSettingsClick(
        val isEditingSettings: Boolean
    ) : NewsHomeReaderAction

    data class OnSettingsValueChanged(
        val settings: Settings?,
        val keyValue: KeyValue,
    ): NewsHomeReaderAction

    class OnEditSettingsCancelClick : NewsHomeReaderAction

    data class OnOpmlImport(
        val ins: InputStream
    ): NewsHomeReaderAction

    data class OnSaveSettingsClick(
        val settings: Settings,
    ) : NewsHomeReaderAction

    data class OnShowInfosClick(
        val isShowInfos: Boolean
    ) : NewsHomeReaderAction

    //
    // News
    //
    data class OnNewsFeedRefresh(
        val feedName: String?,
        val url: String?
    ) : NewsHomeReaderAction

    data class OnNewsFeedClicked(
        val feedName: String,
        val currentFeedConfiguration: NewsFeedConfigurationEntity
    ) : NewsHomeReaderAction

    data class OnNewsItemClicked(
        val newsItem: NewsItem
    ) : NewsHomeReaderAction

    data class OnMarkReadClicked(
        val days: Long
    ) : NewsHomeReaderAction

    class OnNewsItemBackClicked() : NewsHomeReaderAction


    //
    //
    //
    data class OnTabSelected(
        val index: Int? = null,
        val selectedLabel: StringResource? = null,
        val loadData: Boolean = true
    ): NewsHomeReaderAction

    data class OnCollapsibleStateChange(
        val id: String,
        val isExpanded: Boolean
    ): NewsHomeReaderAction

    data class OnScrollPositionChange(
        val id: String,
        val position: Int
    ): NewsHomeReaderAction

    data class OnInitializeTabs(
        val tabLabels: List<StringResource>
    ): NewsHomeReaderAction

    data class OnLanguageSelected(
        val language: Language,
    ): NewsHomeReaderAction

    class OnBusyOkClick : NewsHomeReaderAction
}
