package de.visualdigits.newshomereader.presentation.model

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream
import nl.adaptivity.xmlutil.core.impl.multiplatform.OutputStream
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

    @Immutable
    class OnEditSettingsCancelClick : NewsHomeReaderAction

    @Immutable
    data class OnOpmlImport(
        val fileName: String,
        val ins: InputStream
    ): NewsHomeReaderAction

    @Immutable
    data class OnOpmlExport(
        val fileName: String,
        val outs: OutputStream
    ): NewsHomeReaderAction

    @Immutable
    data class OnSettingsImport(
        val fileName: String,
        val ins: InputStream
    ): NewsHomeReaderAction

    @Immutable
    data class OnSettingsExport(
        val fileName: String,
        val outs: OutputStream
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
    // EditMode
    //
    @Immutable
    data class OnEditModeClick(
        val isEditingMode: Boolean
    ) : NewsHomeReaderAction

    //
    // NewsFeedConfiguration
    //
    @Immutable
    data class OnEditNewsFeedConfigurationClick(
        val originalNewsFeedConfiguration: NewsFeedConfigurationEntity?,
    ) : NewsHomeReaderAction

    @Immutable
    data class OnEditNewsFeedConfigurationOkClick(
        val newsFeedConfiguration: NewsFeedConfiguration?,
    ) : NewsHomeReaderAction

    @Immutable
    class OnEditNewsFeedConfigurationCancelClick : NewsHomeReaderAction

    @Immutable
    class OnAddNewsFeedConfigurationClick(
        val newsFeedGroupName: String
    ) : NewsHomeReaderAction

    @Immutable
    data class OnAddNewsFeedConfigurationOkClick(
        val newsFeedConfiguration: NewsFeedConfiguration?,
    ) : NewsHomeReaderAction

    @Immutable
    class OnAddNewsFeedConfigurationCancelClick : NewsHomeReaderAction

    @Immutable
    data class OnNewsFeedConfigurationValueChanged(
        val newsFeedConfiguration: NewsFeedConfiguration?,
        val keyValue: KeyValue,
    ): NewsHomeReaderAction

    @Immutable
    data class OnDeleteNewsFeedConfigurationClick(
        val newsFeedConfiguration: NewsFeedConfigurationEntity?,
    ) : NewsHomeReaderAction

    @Immutable
    class OnDeleteNewsFeedConfigurationOkClick : NewsHomeReaderAction

    @Immutable
    class OnDeleteNewsFeedConfigurationCancelClick : NewsHomeReaderAction

    @Immutable
    class OnNewsFeedConfigurationOkClick(
        val newsFeedConfiguration: NewsFeedConfigurationEntity
    ) : NewsHomeReaderAction

    @Immutable
    class OnNewsFeedConfigurationCancelClick : NewsHomeReaderAction

    //
    // NewsFeedGroup
    //
    @Immutable
    data class OnEditNewsfeedGroupGroupClick(
        val originalNewsFeedGroupName: String,
    ) : NewsHomeReaderAction

    @Immutable
    data class OnEditNewsFeedGroupOkClick(
        val newsFeedGroupName: String
    ) : NewsHomeReaderAction

    @Immutable
    class OnEditNewsFeedGroupCancelClick : NewsHomeReaderAction

    @Immutable
    data class OnAddNewsfeedGroupGroupClick(
        val isAddingNewsfeedGroup: Boolean
    ) : NewsHomeReaderAction

    @Immutable
    data class OnAddNewsFeedGroupOkClick(
        val newsFeedGroupName: String
    ) : NewsHomeReaderAction

    @Immutable
    class OnAddNewsFeedGroupCancelClick : NewsHomeReaderAction

    @Immutable
    data class OnDeleteNewsfeedGroupClick(
        val newsFeedGroupName: String
    ) : NewsHomeReaderAction

    @Immutable
    class OnDeleteNewsfeedGroupOkClick : NewsHomeReaderAction

    @Immutable
    class OnDeleteNewsfeedGroupCancelClick : NewsHomeReaderAction

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
        val feedName: String?,
        val currentFeedConfiguration: NewsFeedConfigurationEntity
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
