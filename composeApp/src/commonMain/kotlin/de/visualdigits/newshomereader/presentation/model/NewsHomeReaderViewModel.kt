package de.visualdigits.newshomereader.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.LogMessage.Companion.log
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.StudioClockColors
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.error_local_wrong_filetype
import de.visualdigits.generated.AppVersion
import de.visualdigits.newshomereader.domain.model.opml.OutlineType
import de.visualdigits.newshomereader.domain.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.domain.mapper.toNewsFeedItem
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogItem
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.toUiText
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NC
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.CatalogRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.presentation.style.BACKGROUND_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.SPOT_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.TEXT_COLOR_DEFAULT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NewsHomeReaderViewModel(
    val connectivityManager: ConnectivityManager,
    val feedRepository: FeedRepository,
    val articleRepository: ArticleRepository,
    val settingsRepository: SettingsRepository,
    val newsFeedConfigurationRepository: NewsFeedConfigurationRepository,
    val catalogRepository: CatalogRepository
) : ViewModel() {

    val scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>> = mutableMapOf()
    var platformType: PlatformType = PlatformType.unknown

    private val _state = MutableStateFlow(NewsHomeReaderState())
    val state = _state.asStateFlow()

    val _editedSettings = MutableStateFlow<Settings?>(null)
    val editedSettings = _editedSettings.asStateFlow()

    init {
        log(Severity.Info, "Application version ${AppVersion().version} initializing...", withTag = "NHR")
        loadData()
        log(Severity.Info, "Application started", withTag = "NHR")
        log(Severity.Debug, "Settings: ${state.value.settings}", withTag = "NHR")

        _state
            .map { SubState(
                currentNewsFeedGroup = it.currentNewsFeedGroup,
                currentNewsFeedName = it.currentNewsFeedName,
                newsItemSearchText = it.newsItemSearchText,
                keyword = it.currentKeywordBucket
            ) }
            .distinctUntilChanged()
            .flatMapLatest { (group, name, searchText, keyword) ->
                val isSearching = !searchText.isNullOrBlank()
                val isKeyword = !keyword.isNullOrBlank()
                val sourceFlow = when {
                    isSearching -> feedRepository.observeNewsFeedItemSearchItems(searchText)
                    isKeyword -> feedRepository.observeNewsFeedItemSearchItems(keyword)
                    group != null || !name.isNullOrBlank() -> {
                        feedRepository.observeFeedItems(group, name)
                    }
                    else -> flowOf(emptyList())
                }

                sourceFlow
                    .debounce(150.milliseconds)
                    .flowOn(Dispatchers.IO)
                    .transform { items ->
                        coroutineScope {
                            val enriched = items.map { newsItem ->
                                async {
                                    if (newsItem.newsArticle != null) return@async newsItem
                                    val articleResult = articleRepository.getFullArticle(newsItem.id)
                                    if (articleResult is Result.Success) newsItem.copy(
                                        newsArticle = articleResult.data
                                    ) else {
                                        newsItem
                                    }
                                }
                            }.awaitAll()

                            if (isSearching) {
                                emit(SearchResult(
                                    enriched = enriched,
                                    searchType = SearchType.search
                                ))
                            } else if (isKeyword) {
                                val currentState = _state.value
                                val hideRead = currentState.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                                val stopWords = group?.let { g -> determineStopWords(g) } ?: currentState.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                                val visible = calculateVisibleNewsItems(enriched, hideRead, stopWords)
                                emit(SearchResult(
                                    enriched = enriched,
                                    visible = visible,
                                    searchType = SearchType.keyword
                                ))
                            } else {
                                val currentState = _state.value
                                val hideRead = currentState.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                                val stopWords = group?.let { g -> determineStopWords(g) } ?: currentState.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                                val visible = calculateVisibleNewsItems(enriched, hideRead, stopWords)
                                emit(SearchResult(
                                    enriched = enriched,
                                    visible = visible,
                                    searchType = SearchType.standard
                                ))
                            }
                        }
                    }
            }
            .flowOn(Dispatchers.Default)
            .onEach { result ->
                _state.update {
                    if (result.searchType == SearchType.search) {
                        it.copy(
                            filteredNewsItems = result.enriched
                        )
                    } else if (result.searchType == SearchType.keyword) {
                        it.copy(
                            currentNewsItems = result.enriched,
                            visibleNewsItems = result.visible
                        )
                    } else {
                        it.copy(
                            currentNewsItems = result.enriched,
                            visibleNewsItems = result.visible
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onCommonAction(action: CommonAction) {
        when (action) {
            is CommonAction.OnScrollPositionChange -> {
                action.id?.also { id ->
                    scrollPosition[id] = Triple(action.position, action.offset, action.scrollIntent)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onAction(action: NewsHomeReaderAction) {
        when (action) {

            //
            // Settings
            //
            is NewsHomeReaderAction.OnEditSettingsClick -> {
                _editedSettings.value = state.value.settings
                _state.update {
                    it.copy(
                        isEditingSettings = action.isEditingSettings,
                        isShowInfos = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is NewsHomeReaderAction.OnSettingsValueChanged -> {
                if (action.keyValue.descriptor.key == SK.language) {
                    action.keyValue.value?.also { l ->
                        Locale.setDefault((l as Language).locale)
                    }
                }
                _editedSettings.update { current ->
                    current?.copy(
                        key = action.keyValue.descriptor.key as SK,
                        value = action.keyValue.value
                    )
                }
            }

            is NewsHomeReaderAction.OnEditSettingsCancelClick -> {
                _state.update { state ->
                    state.settings?.get<Language>(SK.language)?.also { l -> Locale.setDefault(l.locale) }
                    state.copy(
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is NewsHomeReaderAction.OnSaveSettingsClick -> {
                saveSettings(_editedSettings.value)
            }

            is NewsHomeReaderAction.OnOpmlImport -> {
                importOpml(action.fileName, action.ins)
            }

            is NewsHomeReaderAction.OnOpmlExport -> {
                exportOpml(action.fileName, action.outs)
            }

            is NewsHomeReaderAction.OnSettingsImport -> {
                importSettings(action.fileName, action.ins)
            }

            is NewsHomeReaderAction.OnSettingsExport -> {
                exportSettings(action.fileName, action.outs)
            }

            is NewsHomeReaderAction.UpdateMaxImageSize -> {
                action.settings?.also { settings ->
                    saveSettings(settings.copy(SK.maxImageSize, action.maxImageSize))
                }
                _state.update {
                    it.copy(
                        maxImageSize = action.maxImageSize
                    )
                }
            }

            //
            // EditMode
            //
            is NewsHomeReaderAction.OnEditModeClick -> {
                _state.update {
                    it.copy(
                        isEditMode = action.isEditingMode,
                        isShowInfos = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            //
            // NewsFeedConfiguration
            //
            is NewsHomeReaderAction.OnEditNewsFeedConfigurationClick -> {
                val newsFeedConfiguration = action.originalNewsFeedItem?.toNewsFeedConfiguration(state.value.newsFeedGroups)
                _state.update {
                    it.copy(
                        isEditingNewsFeedConfiguration = true,
                        originalNewsFeedConfiguration = newsFeedConfiguration,
                        editedNewsFeedConfiguration = newsFeedConfiguration
                    )
                }
            }
            is NewsHomeReaderAction.OnEditNewsFeedConfigurationOkClick -> {
                editNewsFeedConfiguration(state.value.originalNewsFeedConfiguration , action.newsFeedConfiguration)
            }
            is NewsHomeReaderAction.OnEditNewsFeedConfigurationCancelClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = false,
                        isEditingNewsFeedConfiguration = false,
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsFeedConfigurationClick -> {
                val newsFeedConfiguration = NewsFeedConfiguration(
                    newsFeedGroups = state.value.newsFeedGroups,
                    values = mapOf(
                        NC.feedName to "",
                        NC.mainGroupName to (action.newsFeedGroup.parentGroupName?:action.newsFeedGroup.name),
                        NC.subGroupName to if (action.newsFeedGroup.parentGroupName != null) action.newsFeedGroup.name else null,
                        NC.url to "",
                        NC.stopWords to ""
                    )
                )

                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = true,
                        originalNewsFeedConfiguration = null,
                        editedNewsFeedConfiguration = newsFeedConfiguration
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsFeedConfigurationOkClick -> {
                viewModelScope.launch {
                    val newsFeedItem = action.newsFeedConfiguration?.toNewsFeedItem()
                    val upsertResult = upsertNewsFeedItem(newsFeedItem)
                    if (upsertResult is Result.Success) {
                        refreshNewsFeeds()
                        _state.update {
                            it.copy(
                                isAddingNewsFeedConfiguration = false,
                                isEditingNewsFeedConfiguration = false,
                                newsFeedGroups = upsertResult.data?:listOf()
                            )
                        }
                    } else if (upsertResult is Result.Error) {
                        log(Severity.Error, "Could not add newsfeed item '${newsFeedItem?.name}'", upsertResult.throwable, withTag = "NHR")
                        _state.update {
                            it.copy(
                                isAddingNewsFeedConfiguration = false,
                                isEditingNewsFeedConfiguration = false,
                                uiMessage = upsertResult.error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
                }
            }

            is NewsHomeReaderAction.OnAddNewsFeedConfigurationCancelClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = false,
                        isEditingNewsFeedConfiguration = false,
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsFeedConfigurationCancelClick -> {
                _state.update {
                    it.copy(
                        isDeletingNewsFeedConfiguration = false
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsFeedConfigurationClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = false,
                        isEditingNewsFeedConfiguration = false,
                        isAddingNewsFeedGroup = false,
                        isEditingNewsFeedGroup = false,
                        isDeletingNewsFeedConfiguration = true,
                        deleteNewsFeedItem = action.newsFeedItem
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsFeedConfigurationOkClick -> {
                viewModelScope.launch {
                    deleteNewsFeedItem(state.value.deleteNewsFeedItem)
                        .onSuccess { newsFeedGroups ->
                            _state.update {
                                it.copy(
                                    isDeletingNewsFeedConfiguration = false,
                                    newsFeedGroups = newsFeedGroups ?: listOf()
                                )
                            }
                        }
                        .onError { _, throwable ->
                            log(Severity.Error, "Could not add newsfeed configuration '${state.value.deleteNewsFeedItem?.name}'", throwable, withTag = "NHR")
                        }
                }
            }
            is NewsHomeReaderAction.OnNewsFeedConfigurationOkClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = false,
                        isEditingNewsFeedConfiguration = false,
                    )
                }
            }
            is NewsHomeReaderAction.OnNewsFeedConfigurationCancelClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = false,
                        isEditingNewsFeedConfiguration = false,
                    )
                }
            }
            is NewsHomeReaderAction.OnNewsFeedConfigurationValueChanged -> {
                _state.update { state ->
                    val key = action.keyValue.descriptor.key as NC
                    var newsFeedConfiguration = state.editedNewsFeedConfiguration?.copy(
                        key = key,
                        value = action.keyValue.value
                    )
                    if (key == NC.mainGroupName) {
                        val newMainGroupName = newsFeedConfiguration?.get<String>(NC.mainGroupName)?:""
                        val newSubGroupName = newsFeedConfiguration?.mainNewsFeedGroupsMap
                            ?.get(newMainGroupName)?.subGroups
                            ?.map { sg ->  sg.name }
                            ?.firstOrNull()
                        newsFeedConfiguration = newsFeedConfiguration?.copy(
                            key = NC.subGroupName,
                            value = newSubGroupName
                        )
                    }
                    state.copy(
                        editedNewsFeedConfiguration = newsFeedConfiguration,
                    )
                }
            }

            //
            // NewsFeedGroup
            //
            is NewsHomeReaderAction.OnEditNewsfeedGroupGroupClick -> {
                _state.update {
                    it.copy(
                        isEditingNewsFeedGroup = true,
                        originalNewsFeedGroup = action.originalNewsFeedGroup,
                    )
                }
            }
            is NewsHomeReaderAction.OnEditNewsFeedGroupOkClick -> {
                editNewsFeedGroup(
                    newsFeedGroup = state.value.originalNewsFeedGroup,
                    editedNewsFeedGroupName = action.editedNewsFeedGroupName
                )
            }
            is NewsHomeReaderAction.OnEditNewsFeedGroupCancelClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedGroup = false,
                        isEditingNewsFeedGroup = false,
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick -> {
                _state.update {
                    it.copy(
                        parentNewsFeedGroup = action.newsFeedGroup,
                        originalNewsFeedGroup = null,
                        currentNewsFeedGroupToDelete = null,
                        isAddingNewsFeedGroup = true
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsFeedGroupOkClick -> {
                addNewsFeedGroup(
                    parentGroup = state.value.parentNewsFeedGroup,
                    newsFeedGroupName = action.newsFeedGroupName
                )
            }
            is NewsHomeReaderAction.OnAddNewsFeedGroupCancelClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedGroup = false,
                        isEditingNewsFeedGroup = false,
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsfeedGroupCancelClick -> {
                _state.update {
                    it.copy(
                        isDeletingNewsFeedGroup = false,
                        currentNewsFeedGroupToDelete = null
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsfeedGroupClick -> {
                _state.update {
                    it.copy(
                        isDeletingNewsFeedGroup = true,
                        currentNewsFeedGroupToDelete = action.newsFeedGroup
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsfeedGroupOkClick -> {
                deleteNewsFeedGroup(state.value.currentNewsFeedGroupToDelete)
            }

            //
            // News
            //
            is NewsHomeReaderAction.OnNewsFeedRefresh -> {
                refreshNewsFeed(action.feedName, action.url)
            }

            is NewsHomeReaderAction.OnNewsFeedsRefresh -> {
                refreshNewsFeeds()
            }

            is NewsHomeReaderAction.OnNewsFeedClicked -> {
                loadFeedItems(action.currentFeedItem)
            }

            is NewsHomeReaderAction.OnNewsItemClicked -> {
                loadArticle(action.newsItem)
            }

            is NewsHomeReaderAction.OnNewsItemClosed -> {
                _state.update {
                    it.copy(
                        currentNewsArticle = null
                    )
                }
            }

            is NewsHomeReaderAction.OnMarkReadClicked -> {
                markItemsAsRead(action.days)
            }

            is NewsHomeReaderAction.OnNewsItemBackClicked -> {
                val current = scrollPosition["newsfeed_items"]
                scrollPosition["newsfeed_items"] = Triple(current?.first?:0, current?.second, ScrollIntent.standard)
                _state.update {
                    it.copy(
                        currentNewsItem = null,
                        currentNewsArticle = null,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is NewsHomeReaderAction.OnNewsItemSearchExpandStateChanged -> {
                _state.update {
                    it.copy(
                        isNewsItemSearchActive = action.expanded,
                    )
                }
            }

            is NewsHomeReaderAction.OnNewsItemSearchTextChanged -> {
                _state.update {
                    it.copy(
                        newsItemSearchText = action.text
                    )
                }
            }

            //
            //
            //
            is NewsHomeReaderAction.OnCollapsibleStateChange -> {
                if (action.id == "group_newsfeeds_navigation") {
                    scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.scrollToStart)
                }
                _state.update {
                    it.copy(
                        collapsibleState = it.collapsibleState + (action.id to action.isExpanded)
                    )
                }
            }

            is NewsHomeReaderAction.OnNewsFeedGroupCollapsibleStateChange -> {
                val stayInGroup = !action.isExpanded &&
                        state.value.currentNewsFeedName != null &&
                        state.value.previousNewsFeedGroup == state.value.currentNewsFeedGroup
                if (action.isExpanded && !stayInGroup && (action.group.outlineType != OutlineType.root)) {
                    scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.scrollToStart)
                } else {
                    scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.standard)
                }
                _state.update {
                    // keep collapsible box open when user switches from single feed to group
                    val newCollapsibleState = if (
                        it.isEditMode ||
                        action.group.subGroups.isNotEmpty() ||
                        action.group.newsFeeds.isNotEmpty()
                    ) {
                        it.collapsibleState + if (stayInGroup) {
                            ("group_${action.group.name}" to true)
                        } else {
                            ("group_${action.group.name}" to action.isExpanded)
                        }
                    } else {
                        it.collapsibleState
                    }
                    it.copy(
                        previousNewsFeedGroup = it.currentNewsFeedGroup,
                        newsItemSearchText = null,
                        currentKeywordBucket = null,
                        currentNewsFeedGroup = if (action.isExpanded || stayInGroup) action.group else null,
                        allowClearVisibleNewsItems = if (stayInGroup) false else !action.isExpanded,
                        currentNewsFeedName = null,
                        collapsibleState = newCollapsibleState
                    )
                }
            }

            is NewsHomeReaderAction.OnCatalogClicked -> {
                loadCatalog(action.isExpanded)
            }

            is NewsHomeReaderAction.OnSubscriptionChanged -> {
                maintainSubscription(action.newsFeedCatalogItem, action.subscribe)
            }

            is NewsHomeReaderAction.OnOnlySubscribedFeeds -> {
                _state.update {
                    it.copy(
                        onlySubscribedFeeds = action.onlySubscribedFeeds
                    )
                }
            }

            is NewsHomeReaderAction.OnCatalogSearchTextChanged -> {
                _state.update {
                    it.copy(
                        catalogSearchText = action.text
                    )
                }
                filterCatalog(action.text)
            }

            is NewsHomeReaderAction.OnLanguageSelected -> {
                Locale.setDefault(action.language.locale)
                _state.update {
                    it.copy(
                        language = action.language
                    )
                }
            }

            is NewsHomeReaderAction.OnShowInfosClick -> {
                _state.update {
                    it.copy(
                        isShowInfos = action.isShowInfos,
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
        }
    }

    private fun maintainSubscription(newsFeedCatalogItem: NewsFeedCatalogItem, subscribe: Boolean) = viewModelScope.launch {
        if (subscribe) {
            val mainCategory = newsFeedCatalogItem.parentCategory?.parentCategory
            val persistedMainGroup = if (mainCategory != null) {
                val addResult = addGroup(
                    NewsFeedGroup(
                        name = mainCategory.name,
                    )
                )
                if (addResult is Result.Success) {
                    addResult.data
                } else if (addResult is Result.Error) {
                    log(Severity.Error, "Could not add root group '${mainCategory.name}'", addResult.throwable, withTag = "NHR")
                    null
                } else {
                    null
                }
            } else {
                null
            }

            val subCategory = newsFeedCatalogItem.parentCategory
            val persistedSubGroup = if (subCategory != null) {
                val addResult = addGroup(
                    if (persistedMainGroup != null) {
                        NewsFeedGroup(
                            parentId = persistedMainGroup.id,
                            parentGroupName = mainCategory?.name,
                            name = subCategory.name,
                        )
                    } else {
                        NewsFeedGroup(
                            parentId = null,
                            parentGroupName = null,
                            name = subCategory.name,
                        )
                    }
                )
                if (addResult is Result.Success) {
                    addResult.data
                } else if (addResult is Result.Error) {
                    log(Severity.Error, "Could not add root group '${mainCategory?.name}'", addResult.throwable, withTag = "NHR")
                    null
                } else {
                    null
                }
            } else {
                null
            }
            val result = upsertNewsFeedItem(newsFeedCatalogItem.toNewsFeedItem().copy(
                mainGroupName = if (persistedMainGroup != null) persistedMainGroup.name else persistedSubGroup?.name?:error("No main group given"),
                subGroupName = if (persistedMainGroup != null) persistedSubGroup?.name else null,
            ))
            if (result is Result.Success) {
                refreshNewsFeeds()
                newsFeedConfigurationRepository.getNewsFeedGroups()
                    .onSuccess { newsFeedGroups ->
                        _state.update {
                            it.copy(
                                newsFeedGroups = newsFeedGroups
                            )
                        }
                    }.onError { error, throwable ->
                        log(Severity.Error, "Could not get newsfeed groups", throwable, withTag = "NHR")
                        _state.update {
                            it.copy(
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
                } else if (result is Result.Error) {
                    log(Severity.Error, "Could not upsert news feed configuration", result.throwable, withTag = "NHR")
                    _state.update {
                        it.copy(
                            uiMessage = result.error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            val newsFeedItem = newsFeedCatalogItem.toNewsFeedItem()
            deleteNewsFeedItem(newsFeedItem)
                .onSuccess { newsFeedGroups ->
                    _state.update {
                        it.copy(
                            newsFeedGroups = newsFeedGroups ?: listOf()
                        )
                    }
                }
                .onError { error, throwable ->
                    log(Severity.Error, "Could not delete feed configuration", throwable, withTag = "NHR")
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        }
    }

    private fun addNewsFeedGroup(
        parentGroup: NewsFeedGroup?,
        newsFeedGroupName: String
    ) = viewModelScope.launch {
        val addResult = newsFeedConfigurationRepository.upsertNewsFeedGroup(
            NewsFeedGroup(
                parentId = parentGroup?.id,
                parentGroupName = parentGroup?.name,
                name = newsFeedGroupName
            )
        )
        when (addResult) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedGroup = false,
                        isEditingNewsFeedGroup = false,
                        newsFeedGroups = addResult.data
                    )
                }
            }

            is Result.Error -> {
                log(Severity.Error, "Could not add newsfeed group '$newsFeedGroupName'", addResult.throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        isAddingNewsFeedGroup = false,
                        isEditingNewsFeedGroup = false,
                    )
                }
            }
        }
    }

    private suspend fun addGroup(
        newsFeedGroup: NewsFeedGroup
    ): Result<NewsFeedGroup?, DataError.Local> {
        log(Severity.Info, "add group '${newsFeedGroup.parentGroupName}/${newsFeedGroup.name}'", withTag = "NHR")
        val getResult = newsFeedConfigurationRepository.getNewsFeedGroupByName(newsFeedGroup.parentGroupName, newsFeedGroup.name)
        return if (getResult is Result.Success) {
            if(getResult.data == null) {
                newsFeedConfigurationRepository.upsertNewsFeedGroupSingle(newsFeedGroup)
            } else {
                Result.Success(getResult.data)
            }
        } else if (getResult is Result.Error) {
            log(Severity.Error, "Could not get root category '${newsFeedGroup.name}'", getResult.throwable, withTag = "NHR")
            Result.Error(DataError.Local.UNKNOWN, getResult.throwable)
        } else {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    private fun editNewsFeedGroup(
        newsFeedGroup: NewsFeedGroup?,
        editedNewsFeedGroupName: String
    ) = viewModelScope.launch {
        val editResult = newsFeedConfigurationRepository.editNewsFeedGroup(
            newsFeedGroup,
            editedNewsFeedGroupName
        )
        if (editResult is Result.Success) {
            _state.update {
                it.copy(
                    isAddingNewsFeedGroup = false,
                    isEditingNewsFeedGroup = false,
                    newsFeedGroups = editResult.data
                )
            }
        } else if (editResult is Result.Error) {
            log(Severity.Error, "Could not get old newsfeed group '${newsFeedGroup?.name}'", editResult.throwable, withTag = "NHR")
            _state.update {
                it.copy(
                    isAddingNewsFeedGroup = false,
                    isEditingNewsFeedGroup = false,
                    uiMessage = editResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private suspend fun upsertNewsFeedItem(newsFeedItem: NewsFeedItem?): Result<List<NewsFeedGroup>?, DataError.Local> {
        return newsFeedItem
            ?.let { nfc ->
                newsFeedConfigurationRepository.upsertNewsFeedItem(nfc)
            }
            ?: Result.Success(null)
    }

    private fun editNewsFeedConfiguration(oldNewsFeedConfiguration: NewsFeedConfiguration?, newNewsFeedConfiguration: NewsFeedConfiguration?) = viewModelScope.launch {
        val oldEntity = oldNewsFeedConfiguration?.toNewsFeedItem()
        val newEntity = newNewsFeedConfiguration?.toNewsFeedItem()

        if (oldEntity != null && newEntity != null) {
            newsFeedConfigurationRepository.editNewsFeedItem(
                oldNewsFeedItem = oldEntity,
                newNewsFeedItem = newEntity
            )
                .onSuccess { newsFeedGroups ->
                    _state.update {
                        it.copy(
                            currentNewsFeedItem = newEntity,
                            isAddingNewsFeedConfiguration = false,
                            isEditingNewsFeedConfiguration = false,
                            newsFeedGroups = newsFeedGroups,
                            visibleNewsItems = calculateVisibleNewsItems(
                                newsItems = it.currentNewsItems,
                                hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                                stopWords = newNewsFeedConfiguration?.get<List<String>>(NC.stopWords)?.toSet()?:setOf()
                            )
                        )
                    }
                }
                .onError { _, throwable ->
                    log(Severity.Error, "Could not modify newsfeed configuration from '${oldEntity.name}' to '${newEntity.name}'", throwable, withTag = "NHR")
                }
        } else {
            _state.update {
                it.copy(
                    isAddingNewsFeedConfiguration = false,
                    isEditingNewsFeedConfiguration = false,
                )
            }
        }
    }

    private fun deleteNewsFeedGroup(newsFeedGroup: NewsFeedGroup?) = viewModelScope.launch {
        if (newsFeedGroup != null) {
            val deleteResult = newsFeedConfigurationRepository.deleteNewsFeedGroup(newsFeedGroup)
            if (deleteResult is Result.Success) {
                _state.update {
                    it.copy(
                        isDeletingNewsFeedGroup = false,
                        currentNewsArticle = null,
                        newsFeedGroups = deleteResult.data
                    )
                }
            } else if (deleteResult is Result.Error) {
                log(Severity.Error, "Could not add newsfeed group '${newsFeedGroup.name}'", deleteResult.throwable, withTag = "NHR")
            }
        }
    }

    private suspend fun deleteNewsFeedItem(newsFeedItem: NewsFeedItem?): Result<List<NewsFeedGroup>?, DataError.Local> {
        return if (newsFeedItem != null) {
            newsFeedConfigurationRepository.deleteNewsFeedItem(newsFeedItem)
        } else {
            Result.Success(null)
        }
    }

    private fun refreshNewsFeed(
        feedName: String?,
        url: String?
    ) = viewModelScope.launch {
//        scrollPosition["newsfeed_items"] = Triple(0, 0, ScrollIntent.scrollToStart)
        val wifiOnly = state.value.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        val loadArticles = state.value.settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue?:false
        val prefetchImages = state.value.settings?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue?:false
        val keepReadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue?:30
        val keepUnreadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue?:30
        val maxImageSize = state.value.settings?.get<Int>(SK.maxImageSize)?:1200
        feedName?.also { fn ->
            url?.also { u ->
                val feedResult = if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                    feedRepository.refreshNewsFeed(
                        feedName = fn,
                        url = u,
                        wifiOnly = wifiOnly,
                        keepReadArticlesInDays = keepReadArticles,
                        keepUnreadArticlesInDays = keepUnreadArticles,
                        maxImageSize = maxImageSize,
                        loadArticles = loadArticles
                    ) { progress, progressStage ->
                        viewModelScope.launch {
                            _state.update {
                                it.copy(
                                    currentProgress = progress,
                                    progressStage = progressStage
                                )
                            }
                        }
                    }
                } else {
                    feedRepository.getNewsFeedByFeedName(feedName)
                }
                if (feedResult is Result.Success) {
                    val (newsFeed, changed) = feedResult.data
                    _state.update {
                        it.copy(
                            currentNewsFeedName = fn,
                            currentProgress = 0.0f,
                            progressStage = ProgressStage.NONE,
                            currentNewsItems = newsFeed?.items?.toList()?:listOf(), // force repaint
                            visibleNewsItems = calculateVisibleNewsItems(
                                newsItems = newsFeed?.items ?: listOf(),
                                hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                                stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                            ),
                            uiMessage = null,
                            uiMessageSeverity = null
                        )
                    }
                    if (prefetchImages) {
                        prefetchImages(changed, listOfNotNull(newsFeed))
                            .onSuccess {
                                _state.update {
                                    it.copy(
                                        currentProgress = 0.0f,
                                        progressStage = ProgressStage.NONE,
                                    )
                                }
                            }
                            .onError { _, throwable ->
                                log(Severity.Error, "Could not prefetch images", throwable, withTag = "NHR")
                            }
                    }
                } else if (feedResult is Result.Error) {
                    log(Severity.Error, "Could not load feed '$feedName'", feedResult.throwable, withTag = "NHR")
                    _state.update {
                        it.copy(
                            currentProgress = 0.0f,
                            progressStage = ProgressStage.NONE,
                            uiMessage = feedResult.error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
            }
        }
    }

    private fun refreshNewsFeeds() = viewModelScope.launch {
        val prefetchImages = state.value.settings?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue ?: false
        val result = newsFeedConfigurationRepository.getNewsFeedGroups()
        if (result is Result.Success) {
            val newsFeedGroups = result.data
            val newsFeedConfigurations = newsFeedGroups.flatMap { nfg ->
                nfg.newsFeeds + nfg.subGroups.flatMap { sg -> sg.newsFeeds}
            }.filter { nfi -> nfi.outlineType != OutlineType.keyword }
            val newsFeedResult = refreshNewsFeeds(newsFeedGroups, newsFeedConfigurations)
            if (newsFeedResult is Result.Success) {
                val (newsFeeds, changed) = newsFeedResult.data
                _state.update {
                    val currentNewsItems = newsFeeds.find { nf -> nf.feedName == it.currentNewsFeedName }?.items ?: listOf()
                    it.currentNewsFeedGroup
                    it.copy(
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        isEditingSettings = false,
                        currentNewsItems = currentNewsItems,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = currentNewsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                            stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                        ),
                        newsFeedGroups = newsFeedGroups
                    )
                }
                if (prefetchImages) {
                    prefetchImages(changed, newsFeeds)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    currentProgress = 0.0f,
                                    progressStage = ProgressStage.NONE,
                                )
                            }
                        }
                        .onError { _, throwable ->
                            log(Severity.Error, "Could not prefetch images", throwable, withTag = "NHR")
                        }
                }
            } else if (newsFeedResult is Result.Error) {
                log(Severity.Error, "Could not refresh newsfeeds'", newsFeedResult.throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                    )
                }
            }
        }
    }

    private fun importSettings(fileName: String, ins: InputStream) = viewModelScope.launch {
        log(Severity.Info, "Importing settings", withTag = "NHR")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            settingsRepository.importSettings(ins)
                .onSuccess { settings ->
                    _state.update {
                        it.copy(
                            settings = settings,
                            isEditingSettings = false,
                            uiMessage = null,
                        )
                    }
                }
                .onError { error, throwable ->
                    log(Severity.Error, "Could not import settings", throwable, withTag = "NHR")
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportSettings(fileName: String, outs: OutputStream) = viewModelScope.launch {
        log(Severity.Info, "Exporting settings", withTag = "NHR")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            val settings = state.value.settings
            if(settings != null) {
                settingsRepository.exportSettings(settings, outs)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                uiMessage = null,
                            )
                        }
                    }
                    .onError { error, throwable ->
                        log(Severity.Error, "Could not export settings", throwable, withTag = "NHR")
                        _state.update {
                            it.copy(
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
            }
        } else {
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun importOpml(fileName: String, ins: InputStream) = viewModelScope.launch {
        log(Severity.Info, "Importing opml...", withTag = "NHR")
        val prefetchImages = state.value.settings?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue ?: false
        if (fileName.endsWith(".opml", ignoreCase = true)) {
            val newFeedConfigurationResult = newsFeedConfigurationRepository.setNewsFeedGroups(ins)
            if (newFeedConfigurationResult is Result.Success) {
                val feedResult = newsFeedConfigurationRepository.getNewsFeedGroups()
                val newsFeedGroups = if (feedResult is Result.Success) {
                    feedResult.data
                } else {
                    state.value.newsFeedGroups
                }
                val newsFeedConfigurations = newsFeedGroups.flatMap { nfg ->
                    nfg.newsFeeds + nfg.subGroups.flatMap { sg -> sg.newsFeeds }
                }.filter { nfi -> nfi.outlineType != OutlineType.keyword }
                val newsFeedResult = refreshNewsFeeds(newsFeedGroups, newsFeedConfigurations)
                if (newsFeedResult is Result.Success) {
                    val (newsFeeds, changed) = newsFeedResult.data
                    _state.update {
                        val currentNewsItems = newsFeeds.find { nf -> nf.feedName == it.currentNewsFeedName }?.items ?: listOf()
                        it.copy(
                            currentProgress = 0.0f,
                            progressStage = ProgressStage.NONE,
                            isEditingSettings = false,
                            currentNewsItems = currentNewsItems,
                            visibleNewsItems = calculateVisibleNewsItems(
                                newsItems = currentNewsItems,
                                hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                                stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                            ),
                            newsFeedGroups = newsFeedGroups
                        )
                    }
                    if (prefetchImages) {
                        prefetchImages(changed, newsFeeds)
                            .onSuccess {
                                _state.update {
                                    it.copy(
                                        currentProgress = 0.0f,
                                        progressStage = ProgressStage.NONE,
                                        uiMessage = null,
                                    )
                                }
                            }
                            .onError { _, throwable ->
                                log(Severity.Error, "Could not prefetch images", throwable, withTag = "NHR")
                            }
                    }

                    val syncResult = feedRepository.synchroniseReadNewsItems()
                    if (syncResult is Result.Error) {
                        log(Severity.Error, "WebDAV Sync failed", syncResult.throwable, withTag = "NHR")
                    }
                } else if (newsFeedResult is Result.Error) {
                    log(Severity.Error, "Could not import OPML", newsFeedResult.throwable, withTag = "NHR")
                    _state.update {
                        it.copy(
                            currentProgress = 0.0f,
                            progressStage = ProgressStage.NONE,
                            uiMessage = newsFeedResult.error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
            } else if (newFeedConfigurationResult is Result.Error){
                log(Severity.Error, "Could not import OPML", newFeedConfigurationResult.throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        uiMessage = newFeedConfigurationResult.error.toUiText(),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportOpml(fileName: String, outs: OutputStream) = viewModelScope.launch {
        log(Severity.Info, "Exporting opml...", withTag = "NHR")
        if (fileName.endsWith(".opml", ignoreCase = true)) {
            newsFeedConfigurationRepository.saveNewsFeedGroups(outs)
                .onSuccess {
                    _state.update {
                        it.copy(
                            uiMessage = null,
                        )
                    }
                }
                .onError { error, throwable ->
                    log(Severity.Error, "Could not export opml", throwable, withTag = "NHR")
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private suspend fun refreshNewsFeeds(
        newsFeedGroups: List<NewsFeedGroup>,
        newsFeedItems: List<NewsFeedItem>
    ): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> {
        val wifiOnly = state.value.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        val loadArticles = state.value.settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
        val keepReadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
        val keepUnreadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
        val maxImageSize = state.value.settings?.get<Int>(SK.maxImageSize) ?: 1200

//        scrollPosition["newsfeed_items"] = Triple(0, 0, ScrollIntent.scrollToStart)

        return if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
            val newsFeedsResult = feedRepository.refreshNewsFeeds(
                newsFeedItems = newsFeedItems,
                progress = { progress, progressStage ->
                    _state.update {
                        it.copy(
                            currentProgress = progress,
                            progressStage = progressStage
                        )
                    }
                }
            )
            if (newsFeedsResult is Result.Success) {
                val (newsFeeds, newsItems) = newsFeedsResult.data
                _state.update {
                    it.copy(
                        currentNewsItems = newsItems,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = newsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                            stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                        ),
                        newsFeedGroups = newsFeedGroups,
                        isEditingSettings = false
                    )
                }
                feedRepository.refreshNewsFeedItems(
                    newsFeeds = newsFeeds,
                    newsItems = newsItems,
                    wifiOnly = wifiOnly,
                    keepReadArticlesInDays = keepReadArticles,
                    keepUnreadArticlesInDays = keepUnreadArticles,
                    maxImageSize = maxImageSize,
                    loadArticles = loadArticles,
                    progress = { progress, progressStage ->
                        _state.update {
                            it.copy(
                                currentProgress = progress,
                                progressStage = progressStage
                            )
                        }
                    }
                )
            } else if (newsFeedsResult is Result.Error) {
                log(Severity.Error, "Could not refresh news feeds", newsFeedsResult.throwable, withTag = "NHR")
                feedRepository.getAllNewsFeeds()
            } else {
                log(Severity.Info, "Could not get news feeds from remote - fetching newsFeeds from database", withTag = "NHR")
                feedRepository.getAllNewsFeeds()
            }
        } else {
            log(Severity.Info, "No free of charge internet connection available - fetching newsFeeds from database", withTag = "NHR")
            feedRepository.getAllNewsFeeds()
        }

    }

    private suspend fun prefetchImages(
        changed: Boolean,
        newsFeeds: List<NewsFeed>
    ): Result<Unit, DataError.Remote> {
        return if (changed) {
            feedRepository.prefetchImages(
                newsFeeds = newsFeeds,
                progress = { progress, progressStage ->
                    _state.update {
                        it.copy(
                            currentProgress = progress,
                            progressStage = progressStage
                        )
                    }
                }
            ).onSuccess {
                _state.update {
                    it.copy(
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE
                    )
                }
            }
            .onError { error, throwable ->
                log(Severity.Error, "Could not refresh images", throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        uiMessage = error.toUiText(),
                        uiMessageSeverity = Severity.Error,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE
                    )
                }
            }
        } else {
            Result.Success(Unit)
        }
    }

    private fun loadCatalog(isExpanded: Boolean) = viewModelScope.launch {
        catalogRepository.loadCatalog()
            .onSuccess { newsFeedCatalog ->
                _state.update {
                    it.copy(
                        isViewingCatalog = isExpanded,
                        newsFeedCatalog = newsFeedCatalog,
                        filteredCatalog = newsFeedCatalog
                    )
                }
            }
            .onError { _, throwable ->
                log(Severity.Error, "Could not log catalog", throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        isViewingCatalog = false,
                        newsFeedCatalog = null,
                        filteredCatalog = null
                    )
                }
            }
    }

    private fun loadData() = viewModelScope.launch {
        _state.update {
            it.copy(
                currentProgress = 0.0f,
                progressStage = ProgressStage.NONE,
            )
        }
        val result = settingsRepository.getSettings()
        if (result is Result.Success) {
            val settings = result.data
            val finalSettings = if (settings != null) {
                settings
            } else {
                val newSettings = Settings(mapOf(
                    SK.backgroundColor to BACKGROUND_COLOR_DEFAULT,
                    SK.buttonColor to BUTTON_COLOR_DEFAULT,
                    SK.textColor to TEXT_COLOR_DEFAULT,
                    SK.spotColor to SPOT_COLOR_DEFAULT,
                    SK.clockColor to StudioClockColors.STUDIO_CLOCK_COLOR_DEFAULT,
                    SK.language to Language.EN,
                    SK.refreshInterval to RefreshIntervalEnum.MINUTES_60,
                    SK.refreshWifiOnly to BooleanEnum.TRUE,
                    SK.maxImageSize to 1200,
                    SK.loadArticles to BooleanEnum.FALSE,
                    SK.prefetchImages to BooleanEnum.FALSE,
                    SK.hideRead to BooleanEnum.TRUE,
                    SK.keepReadArticles to KeepArticlesEnum.DAYS_3,
                    SK.keepUnreadArticles to KeepArticlesEnum.DAYS_7
                ))
                settingsRepository.setSettings(newSettings)
                    .onError { _, throwable ->
                        log(Severity.Error, "Could not safe initial settings", throwable, withTag = "NHR")
                    }
                newSettings
            }

            Locale.setDefault(finalSettings.get<Language>(SK.language)?.locale?: Language.EN.locale)

            _state.update {
                it.copy(
                    settings = finalSettings,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = null,
                    uiMessageSeverity = null,
                    collapsibleState = mapOf("group_newsfeeds_navigation" to true)
                )
            }

            val syncResult = feedRepository.synchroniseReadNewsItems()
            if (syncResult is Result.Error) {
                log(Severity.Error, "WebDAV Sync failed", syncResult.throwable, withTag = "NHR")
            }
        } else if (result is Result.Error) {
            log(Severity.Error, "Could not load data", result.throwable, withTag = "NHR")
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = result.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }

        newsFeedConfigurationRepository.getNewsFeedGroups()
            .onSuccess { newsFeedGroups ->
                _state.update {
                    it.copy(
                        newsFeedGroups = newsFeedGroups,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { _, throwable ->
                log(Severity.Error, "Could not get settings", throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
    }

    private fun loadFeedItems(
        newsFeedItem: NewsFeedItem
    ) = viewModelScope.launch {
log(Severity.Info, "loadFeedItems", withTag = "NHR")
        if ((newsFeedItem.outlineType == OutlineType.newsfeed || newsFeedItem.outlineType == OutlineType.keyword) &&
            ((newsFeedItem.outlineType != OutlineType.keyword && state.value.currentNewsFeedName != newsFeedItem.name) ||
                (newsFeedItem.outlineType == OutlineType.keyword && state.value.currentKeywordBucket != newsFeedItem.name) ||
                state.value.previousOutlineType != newsFeedItem.outlineType)
        ) {
log(Severity.Info, "loadFeedItems - SCROLL TO TOP", withTag = "NHR")
            scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.scrollToStart)
        }
        _state.update {
            it.copy(
                newsItemSearchText = null,
                previousOutlineType = newsFeedItem.outlineType,
                currentKeywordBucket = if (newsFeedItem.outlineType == OutlineType.keyword) newsFeedItem.name else null,
                previousNewsFeedName = it.currentNewsFeedName,
                currentNewsFeedName = newsFeedItem.name,
                allowClearVisibleNewsItems = true,
                currentNewsFeedGroup = null,
                currentNewsFeedItem = newsFeedItem,
                currentNewsArticle = null,
                currentProgress = 0.0f,
                progressStage = ProgressStage.NONE,
                uiMessage = null,
                uiMessageSeverity = null
            )
        }

        val syncResult = feedRepository.synchroniseReadNewsItems()
        if (syncResult is Result.Error) {
            log(Severity.Error, "WebDAV Sync failed", syncResult.throwable, withTag = "NHR")
        }
    }

    private fun markItemsAsRead(days: Long) = viewModelScope.launch {
        val threshold = OffsetDateTime.now().minus(days, ChronoUnit.DAYS)
        val newsItems = state.value.currentNewsItems
            .filter { newsItem -> newsItem.updated.isBefore(threshold) }
            .map { newsItem -> newsItem.copy(isRead = true) }
        val markResult = feedRepository.markNewsItemsAsRead(newsItems)
        if (markResult is Result.Success) {
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    allowClearVisibleNewsItems = days == 0L,
                    progressStage = ProgressStage.NONE,
                    currentNewsItems = newsItems,
                    visibleNewsItems = calculateVisibleNewsItems(
                        newsItems = newsItems,
                        hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                        stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                    ),
                )
            }

            val syncResult = feedRepository.synchroniseReadNewsItems()
            if (syncResult is Result.Error) {
                log(Severity.Error, "WebDAV Sync failed", syncResult.throwable, withTag = "NHR")
            }
        } else if (markResult is Result.Error) {
            log(Severity.Error, "Could not load article", markResult.throwable, withTag = "NHR")
            _state.update {
                it.copy(
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = markResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun loadArticle(newsItem: NewsItem) = viewModelScope.launch {
        val articleResult = articleRepository.readFullArticle(newsItem)
        if (articleResult is Result.Success) {
            val copy = newsItem.copy(newsArticle = articleResult.data.first)
            _state.update {
                val currentNewsItems = it.currentNewsItems.map { ni ->
                    if (ni.id == newsItem.id) copy else ni
                }
                it.copy(
                    currentNewsItem = copy,
                    currentNewsArticle = articleResult.data.first,
                    currentNewsItems = currentNewsItems,
                    isNewsItemSearchActive = false,
                    visibleNewsItems = calculateVisibleNewsItems(
                        newsItems = currentNewsItems,
                        hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false,
                        stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                    ),
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = null,
                    uiMessageSeverity = null
                )
            }
        } else if (articleResult is Result.Error) {
            log(Severity.Error, "Could not load article", articleResult.throwable, withTag = "NHR")
            _state.update {
                it.copy(
                    isNewsItemSearchActive = false,
                    newsItemSearchText = null,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = articleResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
        val markResult = feedRepository.markNewsItemsAsRead(listOf(newsItem))
        if (markResult is Result.Success) {
            val syncResult = feedRepository.synchroniseReadNewsItems()
            if (syncResult is Result.Error) {
                log(Severity.Error, "WebDAV Sync failed", syncResult.throwable, withTag = "NHR")
            }
        } else if (markResult is Result.Error) {
            log(Severity.Error, "Could not mark news item as read", markResult.throwable, withTag = "NHR")
        }
    }

    private fun saveSettings(
        editedSettings: Settings?,
    ) = viewModelScope.launch {
        checkNotNull(editedSettings) { "No settings to save" }
        settingsRepository.setSettings(editedSettings)
            .onSuccess {
                _editedSettings.value = null
                _state.update {
                    it.copy(
                        settings = editedSettings,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = it.currentNewsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false,
                            stopWords = it.currentNewsFeedGroup?.let { g -> determineStopWords(g) } ?: it.currentNewsFeedItem?.stopWords?.toSet() ?: setOf()
                        ),
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { error, throwable ->
                log(Severity.Error, "Could not save settings", throwable, withTag = "NHR")
                _state.update {
                    it.copy(
                        currentProgress = 0.0f,
                        isEditingSettings = false,
                        progressStage = ProgressStage.NONE,
                        uiMessage = error.toUiText(),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
    }

    private fun filterCatalog(query: String) {
        val originalCatalog = _state.value.newsFeedCatalog ?: return
        if (query.isBlank()) {
            _state.update { it.copy(filteredCatalog = originalCatalog) }
            return
        }

        val filtered = originalCatalog.copy(
            categories = originalCatalog.categories.mapNotNull { category ->
                val filteredSubs = category.subCategories.mapNotNull { sub ->
                    val filteredFeeds = sub.feeds.filter { feed ->
                        feed.name.contains(query, ignoreCase = true)
                                || feed.descriptionShort.contains(query, ignoreCase = true)
                                || feed.descriptionLong.contains(query, ignoreCase = true)
                    }
                    if (filteredFeeds.isNotEmpty() || sub.name.contains(query, ignoreCase = true)) {
                        sub.copy(feeds = filteredFeeds)
                    } else null
                }
                if (filteredSubs.isNotEmpty() || category.name.contains(query, ignoreCase = true)) {
                    category.copy(subCategories = filteredSubs)
                } else null
            }
        )
        _state.update {
            it.copy(filteredCatalog = filtered)
        }
    }

    private fun determineStopWords(group: NewsFeedGroup?): Set<String> =
        ((group?.newsFeeds?.flatMap { nf -> nf.stopWords } ?: listOf()) +
                (group?.subGroups?.flatMap { sg -> sg.newsFeeds.flatMap { nf -> nf.stopWords } } ?: listOf()))
            .toSet()

    private fun calculateVisibleNewsItems(newsItems: List<NewsItem>, hideRead: Boolean, stopWords: Set<String>): List<NewsItem> {
        val sortedByDescending = newsItems
            .filter { item -> (!hideRead || !item.isRead)
                    && item.title.nostop(stopWords)
                    && item.summary.nostop(stopWords)
            }
            .sortedByDescending { item -> item.updated }
        return sortedByDescending
    }
}

private fun String.nostop(stopWords: Set<String>): Boolean = stopWords.none { w -> this.contains(w, ignoreCase = true) }

private data class SubState(
    val currentNewsFeedGroup: NewsFeedGroup?,
    val currentNewsFeedName: String?,
    val newsItemSearchText: String?,
    val keyword: String?
)

private data class SearchResult(
    val enriched: List<NewsItem>,
    val visible: List<NewsItem> = emptyList(),
    val searchType: SearchType
)

private enum class SearchType {
    search,
    keyword,
    standard
}
