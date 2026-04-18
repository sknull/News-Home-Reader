package de.visualdigits.newshomereader.presentation.model

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream
import org.jetbrains.compose.resources.StringResource

sealed interface NewsHomeReaderAction {

    //
    // Settings
    //

    @Immutable
    data class OnEditSettingsClick(
        val isEditingSettings: Boolean
    ) : NewsHomeReaderAction

    @Immutable
    data class OnSettingsValueChanged(
        val settings: Settings?,
        val keyValue: KeyValue,
    ): NewsHomeReaderAction

    class OnEditSettingsCancelClick : NewsHomeReaderAction

    @Immutable
    data class OnOpmlImport(
        val ins: InputStream
    ): NewsHomeReaderAction

    @Immutable
    data class OnSaveSettingsClick(
        val settings: Settings,
    ) : NewsHomeReaderAction

    @Immutable
    data class OnShowInfosClick(
        val isShowInfos: Boolean
    ) : NewsHomeReaderAction

    @Immutable
    data class UpdateMaxImageSize(
        val settings: Settings?,
        val maxImageSize: Int
    ) : NewsHomeReaderAction

    //
    // News
    //
    @Immutable
    data class OnNewsFeedRefresh(
        val feedName: String?,
        val url: String?
    ) : NewsHomeReaderAction

    @Immutable
    class OnNewsFeedsRefresh : NewsHomeReaderAction

    @Immutable
    data class OnNewsFeedClicked(
        val feedName: String,
        val currentFeedConfiguration: NewsFeedConfiguration
    ) : NewsHomeReaderAction

    @Immutable
    data class OnNewsItemClicked(
        val newsItem: NewsItem
    ) : NewsHomeReaderAction

    @Immutable
    class OnNewsItemClosed : NewsHomeReaderAction

    @Immutable
    data class OnMarkReadClicked(
        val days: Long
    ) : NewsHomeReaderAction

    @Immutable
    class OnNewsItemBackClicked : NewsHomeReaderAction


    //
    //
    //
    @Immutable
    data class OnTabSelected(
        val index: Int? = null,
        val selectedLabel: StringResource? = null,
        val loadData: Boolean = true
    ): NewsHomeReaderAction

    @Immutable
    data class OnCollapsibleStateChange(
        val id: String,
        val isExpanded: Boolean
    ): NewsHomeReaderAction

    @Immutable
    data class OnScrollPositionChange(
        val id: String,
        val position: Int,
        val offset: Int? = null
    ): NewsHomeReaderAction

    @Immutable
    data class OnInitializeTabs(
        val tabLabels: List<StringResource>
    ): NewsHomeReaderAction

    @Immutable
    data class OnLanguageSelected(
        val language: Language,
    ): NewsHomeReaderAction

    @Immutable
    class OnBusyOkClick : NewsHomeReaderAction
}
