package de.visualdigits.newshomereader.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.StudioClockColors
import de.visualdigits.common.presentation.components.applyAppLanguage
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.error_local_wrong_filetype
import de.visualdigits.generated.AppVersion
import de.visualdigits.newshomereader.domain.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.domain.mapper.toNewsFeedItem
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogItem
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.toUiText
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NC
import de.visualdigits.newshomereader.domain.model.newsfeedconfiguration.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.opml.OutlineType
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NewsHomeReaderViewModel(
    private val connectivityManager: ConnectivityManager,
    private val feedRepository: FeedRepository,
    private val articleRepository: ArticleRepository,
    private val settingsRepository: SettingsRepository,
    private val newsFeedConfigurationRepository: NewsFeedConfigurationRepository,
    private val catalogRepository: CatalogRepository,
    scope: CoroutineScope
) : ViewModel() {

    val scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>> = mutableMapOf()
    var platformType: PlatformType = PlatformType.unknown

    private val _state = MutableStateFlow(NewsHomeReaderState())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    
    private val _settings = MutableStateFlow<Settings?>(null)
    val settings = _settings.asStateFlow()
    
    private val _editedSettings = MutableStateFlow<Settings?>(null)
    val editedSettings = _editedSettings.asStateFlow()

     private val _filteredNewsItems = MutableStateFlow<List<NewsItem>>(emptyList())
    val filteredNewsItems = _filteredNewsItems.asStateFlow()

    private val _visibleNewsItems =  MutableStateFlow<List<NewsItem>>(emptyList())
    val visibleNewsItems = _visibleNewsItems.asStateFlow()

    private val _currentNewsItems = MutableStateFlow<Map<String, NewsItem>>(emptyMap())

    init {
        Logger.i("Application version ${AppVersion().version} initializing...")
        loadData()
        Logger.i("Application started")

        val articleSemaphore = Semaphore(3)

        _state
            .map { SubState(
                currentNewsFeedGroup = it.currentNewsFeedGroup,
                currentNewsFeedName = it.currentNewsFeedName,
                newsItemSearchText = it.newsItemSearchText,
                keyword = it.currentKeywordBucket
            ) }
            .distinctUntilChanged()
            .debounce(200.milliseconds)
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
                    .distinctUntilChanged()
                    .transform { items ->
                        if (items.isEmpty() && _currentNewsItems.value.isNotEmpty()) return@transform

                        val enriched = supervisorScope {
                            items.map { newsItem ->
                                async(Dispatchers.IO) {
                                    try {
                                        articleSemaphore.withPermit {
                                            val cached = _currentNewsItems.value[newsItem.uiKey]
                                            if (cached?.newsArticle != null) {
                                                return@async cached.copy(id = newsItem.id)
                                            }

                                            if (newsItem.newsArticle != null) return@async newsItem

                                            if (newsItem.id != 0L) {
                                                val articleResult = withContext(Dispatchers.IO + NonCancellable) {
                                                    articleRepository.getFullArticle(newsItem.id)
                                                }
                                                if (articleResult is Result.Success) {
                                                    val enrichedItem = newsItem.copy(newsArticle = articleResult.data)
                                                    _currentNewsItems.update { current -> current + (newsItem.uiKey to enrichedItem) }
                                                    return@async enrichedItem
                                                }
                                            }

                                            newsItem
                                        }
                                    } catch (e: CancellationException) {
                                        throw e
                                    } catch (e: Exception) {
                                        Logger.e("Something went wrong while refreshing feeds", e)
                                        newsItem
                                    }
                                }
                            }.awaitAll()
                        }

                        if (enriched.size < _currentNewsItems.value.size && _isLoading.value) {
                            return@transform
                        }

                        if (isSearching) {
                            emit(SearchResult(
                                enriched = enriched,
                                searchType = SearchType.search
                            ))
                        } else {
                            val currentState = _state.value
                            val hideRead = _settings.value?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                            val stopWords = group?.let { g -> determineStopWords(g) }
                                ?: currentState.currentNewsFeedItem?.stopWords?.toSet()
                                ?: setOf()

                            val visible = calculateVisibleNewsItems(enriched, hideRead, stopWords)

                            emit(SearchResult(
                                enriched = enriched,
                                visible = visible,
                                searchType = if (isKeyword) SearchType.keyword else SearchType.standard
                            ))
                        }
                    }
            }
            .flowOn(Dispatchers.Default)
            .onEach { result ->
                when (result.searchType) {
                    SearchType.search -> {
                        _filteredNewsItems.update { result.enriched }
                    }
                    else -> {
                        _currentNewsItems.update { result.enriched.associateBy { e -> e.uiKey } }
                        _visibleNewsItems.update { result.visible }
                    }
                }
            }
            .launchIn(scope)
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
                _editedSettings.update { _settings.value }
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
                        applyAppLanguage((l as Language).localeCode)
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
                    _settings.value?.get<Language>(SK.language)?.also { l -> applyAppLanguage(l.localeCode) }
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
                importOpml(action.fileName, action.source)
            }

            is NewsHomeReaderAction.OnOpmlExport -> {
                exportOpml(action.fileName, action.sink)
            }

            is NewsHomeReaderAction.OnSettingsImport -> {
                importSettings(action.fileName, action.source)
            }

            is NewsHomeReaderAction.OnSettingsExport -> {
                exportSettings(action.fileName, action.sink)
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
                        Logger.e("Could not add newsfeed item '${newsFeedItem?.name}'", upsertResult.throwable)
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
                            Logger.e("Could not add newsfeed configuration '${state.value.deleteNewsFeedItem?.name}'", throwable)
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
                closeNewsItem(state.value.currentNewsItem)
            }

            is NewsHomeReaderAction.OnMarkReadClicked -> {
                markItemsAsRead(action.days)
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
                if (action.id == "newsfeed_items" && action.isExpanded) {
                    scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.scrollToStart)
                } else {
                    scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.standard)
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
                if (action.isExpanded && (action.group.outlineType != OutlineType.root)) {
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
                applyAppLanguage(action.language.localeCode)
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
                when (addResult) {
                    is Result.Success -> {
                        addResult.data
                    }

                    is Result.Error -> {
                        Logger.e("Could not add root group '${mainCategory.name}'", addResult.throwable)
                        null
                    }
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
                when (addResult) {
                    is Result.Success -> {
                        addResult.data
                    }

                    is Result.Error -> {
                        Logger.e("Could not add root group '${mainCategory?.name}'", addResult.throwable)
                        null
                    }
                }
            } else {
                null
            }
            val result = upsertNewsFeedItem(newsFeedCatalogItem.toNewsFeedItem().copy(
                mainGroupName = persistedMainGroup?.name ?: (persistedSubGroup?.name?:error("No main group given")),
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
                        Logger.e("Could not get newsfeed groups", throwable)
                        _state.update {
                            it.copy(
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
                } else if (result is Result.Error) {
                    Logger.e("Could not upsert news feed configuration", result.throwable)
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
                    Logger.e("Could not delete feed configuration", throwable)
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
                Logger.e("Could not add newsfeed group '$newsFeedGroupName'", addResult.throwable)
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
        Logger.i("add group '${newsFeedGroup.parentGroupName}/${newsFeedGroup.name}'")
        val getResult = newsFeedConfigurationRepository.getNewsFeedGroupByName(newsFeedGroup.parentGroupName, newsFeedGroup.name)
        return if (getResult is Result.Success) {
            if(getResult.data == null) {
                newsFeedConfigurationRepository.upsertNewsFeedGroupSingle(newsFeedGroup)
            } else {
                Result.Success(getResult.data)
            }
        } else if (getResult is Result.Error) {
            Logger.e("Could not get root category '${newsFeedGroup.name}'", getResult.throwable)
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
            Logger.e("Could not get old newsfeed group '${newsFeedGroup?.name}'", editResult.throwable)
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
                        )
                    }
                }
                .onError { _, throwable ->
                    Logger.e("Could not modify newsfeed configuration from '${oldEntity.name}' to '${newEntity.name}'", throwable)
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
                        currentNewsItem = null,
                        newsFeedGroups = deleteResult.data
                    )
                }
            } else if (deleteResult is Result.Error) {
                Logger.e("Could not add newsfeed group '${newsFeedGroup.name}'", deleteResult.throwable)
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
    ) {
        _isLoading.update { true }
        viewModelScope.launch {
            //        scrollPosition["newsfeed_items"] = Triple(0, 0, ScrollIntent.scrollToStart)
            val wifiOnly = _settings.value?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
            val loadArticles = _settings.value?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
            val prefetchImages = _settings.value?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue ?: false
            val keepReadArticles = _settings.value?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
            val keepUnreadArticles = _settings.value?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
            val maxImageSize = _settings.value?.get<Int>(SK.maxImageSize) ?: 1200
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
                        )
                    } else {
                        feedRepository.getNewsFeedByFeedName(feedName)
                    }
                    if (feedResult is Result.Success) {
                        val (newsFeed, changed) = feedResult.data
                        _isLoading.update { false }
                        _state.update {
                            it.copy(
                                currentNewsFeedName = fn,
                                uiMessage = null,
                                uiMessageSeverity = null
                            )
                        }
                        if (prefetchImages) {
                            prefetchImages(changed, listOfNotNull(newsFeed))
                                .onSuccess {
                                    _isLoading.update { false }
                                }
                                .onError { _, throwable ->
                                    Logger.e("NewsHomeReaderViewModel: Could not prefetch images", throwable)
                                }
                        }
                    } else if (feedResult is Result.Error) {
                        Logger.e("NewsHomeReaderViewModel: Could not load feed '$feedName'", feedResult.throwable)
                        _isLoading.update { false }
                        _state.update {
                            it.copy(
                                uiMessage = feedResult.error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
                }
            }
        }
    }

    private fun refreshNewsFeeds() {
        _isLoading.update { true }
        viewModelScope.launch {
            val prefetchImages = _settings.value?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue ?: false
            val result = newsFeedConfigurationRepository.getNewsFeedGroups()
            if (result is Result.Success) {
                val newsFeedGroups = result.data
                val newsFeedConfigurations = newsFeedGroups.flatMap { nfg ->
                    nfg.newsFeeds + nfg.subGroups.flatMap { sg -> sg.newsFeeds }
                }.filter { nfi -> nfi.outlineType != OutlineType.keyword }
                val newsFeedResult = refreshNewsFeeds(newsFeedGroups, newsFeedConfigurations)
                if (newsFeedResult is Result.Success) {
                    val (newsFeeds, changed) = newsFeedResult.data
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            isEditingSettings = false,
                            newsFeedGroups = newsFeedGroups
                        )
                    }
                    if (prefetchImages) {
                        prefetchImages(changed, newsFeeds)
                            .onSuccess {
                                _isLoading.update { false }
                            }
                            .onError { _, throwable ->
                                Logger.e("Could not prefetch images", throwable)
                            }
                    }
                } else if (newsFeedResult is Result.Error) {
                    Logger.e("Could not refresh newsfeeds'", newsFeedResult.throwable)
                    _isLoading.update { false }
                }
            }
        }
    }

    private fun importSettings(fileName: String, source: Source) = viewModelScope.launch {
        Logger.i("Importing settings")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            settingsRepository.importSettings(source)
                .onSuccess { settings ->
                    _settings.update { settings }
                    _state.update {
                        it.copy(
                            isEditingSettings = false,
                            uiMessage = null,
                        )
                    }
                }
                .onError { error, throwable ->
                    Logger.e("Could not import settings", throwable)
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportSettings(fileName: String, sink: Sink) = viewModelScope.launch {
        Logger.i("Exporting settings")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            val settings = _settings.value
            if(settings != null) {
                settingsRepository.exportSettings(settings, sink)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                uiMessage = null,
                            )
                        }
                    }
                    .onError { error, throwable ->
                        Logger.e("Could not export settings", throwable)
                        _state.update {
                            it.copy(
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
            }
        } else {
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun importOpml(fileName: String, source: Source) = viewModelScope.launch {
        Logger.i("Importing opml...")
        val prefetchImages = _settings.value?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue ?: false
        if (fileName.endsWith(".opml", ignoreCase = true)) {
            val newFeedConfigurationResult = newsFeedConfigurationRepository.setNewsFeedGroups(source)
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
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            isEditingSettings = false,
                            newsFeedGroups = newsFeedGroups
                        )
                    }
                    if (prefetchImages) {
                        prefetchImages(changed, newsFeeds)
                            .onSuccess {
                                _isLoading.update { false }
                                _state.update {
                                    it.copy(
                                        uiMessage = null,
                                    )
                                }
                            }
                            .onError { _, throwable ->
                                Logger.e("Could not prefetch images", throwable)
                            }
                    }

                    val syncResult = feedRepository.synchroniseReadNewsItems()
                    if (syncResult is Result.Error) {
                        Logger.e("WebDAV Sync failed", syncResult.throwable)
                    }
                } else if (newsFeedResult is Result.Error) {
                    Logger.e("Could not import OPML", newsFeedResult.throwable)
                    _isLoading.update { false }
                    _state.update {
                        it.copy(
                            uiMessage = newsFeedResult.error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
            } else if (newFeedConfigurationResult is Result.Error){
                Logger.e("Could not import OPML", newFeedConfigurationResult.throwable)
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        uiMessage = newFeedConfigurationResult.error.toUiText(),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
        } else {
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportOpml(fileName: String, sink: Sink) = viewModelScope.launch {
        Logger.i("Exporting opml...")
        if (fileName.endsWith(".opml", ignoreCase = true)) {
            newsFeedConfigurationRepository.saveNewsFeedGroups(sink)
                .onSuccess {
                    _state.update {
                        it.copy(
                            uiMessage = null,
                        )
                    }
                }
                .onError { error, throwable ->
                    Logger.e("Could not export opml", throwable)
                    _state.update {
                        it.copy(
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            _isLoading.update { false }
            _state.update {
                it.copy(
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
        Logger.i("NewsHomeReaderViewModel: refreshNewsFeeds")
        val wifiOnly = _settings.value?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        val loadArticles = _settings.value?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
        val keepReadArticles = _settings.value?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
        val keepUnreadArticles = _settings.value?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
        val maxImageSize = _settings.value?.get<Int>(SK.maxImageSize) ?: 1200

//        scrollPosition["newsfeed_items"] = Triple(0, 0, ScrollIntent.scrollToStart)

        return if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
            val newsFeedsResult = feedRepository.refreshNewsFeeds(
                newsFeedItems = newsFeedItems
            )
            when (newsFeedsResult) {
                is Result.Success -> {
                    val (newsFeeds, newsItems) = newsFeedsResult.data
                    _state.update {
                        it.copy(
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
                        loadArticles = loadArticles
                    )
                }

                is Result.Error -> {
                    Logger.e("Could not refresh news feeds", newsFeedsResult.throwable)
                    feedRepository.getAllNewsFeeds()
                }

            }
        } else {
            Logger.i("No free of charge internet connection available - fetching newsFeeds from database")
            feedRepository.getAllNewsFeeds()
        }

    }

    private suspend fun prefetchImages(
        changed: Boolean,
        newsFeeds: List<NewsFeed>
    ): Result<Unit, DataError.Remote> {
        return if (changed) {
            feedRepository.prefetchImages(
                newsFeeds = newsFeeds
            ).onSuccess {
                _isLoading.update { false }
            }
            .onError { error, throwable ->
                Logger.e("Could not refresh images", throwable)
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        uiMessage = error.toUiText(),
                        uiMessageSeverity = Severity.Error,
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
                Logger.e("Could not log catalog", throwable)
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
        _isLoading.update { true }
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
                        Logger.e("Could not safe initial settings", throwable)
                    }
                newSettings
            }

            applyAppLanguage(finalSettings.get<Language>(SK.language)?.localeCode?: Language.EN.localeCode)

            _isLoading.update { false }
            _settings.update { settings }
            _editedSettings.update { settings }
            _state.update {
                it.copy(
                    uiMessage = null,
                    uiMessageSeverity = null,
                    collapsibleState = mapOf("newsfeed_items" to true)
                )
            }

            val syncResult = feedRepository.synchroniseReadNewsItems()
            if (syncResult is Result.Error) {
                Logger.e("WebDAV Sync failed", syncResult.throwable)
            }
        } else if (result is Result.Error) {
            Logger.e("Could not load data", result.throwable)
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = result.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }

        newsFeedConfigurationRepository.getNewsFeedGroups()
            .onSuccess { newsFeedGroups ->
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        newsFeedGroups = newsFeedGroups,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { _, throwable ->
                Logger.e("Could not get settings", throwable)
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
    }

    private fun loadFeedItems(
        newsFeedItem: NewsFeedItem
    ) = viewModelScope.launch {
Logger.i("loadFeedItems")
        if ((newsFeedItem.outlineType == OutlineType.newsfeed || newsFeedItem.outlineType == OutlineType.keyword) &&
            ((newsFeedItem.outlineType != OutlineType.keyword && state.value.currentNewsFeedName != newsFeedItem.name) ||
                (newsFeedItem.outlineType == OutlineType.keyword && state.value.currentKeywordBucket != newsFeedItem.name) ||
                state.value.previousOutlineType != newsFeedItem.outlineType)
        ) {
Logger.i("loadFeedItems - SCROLL TO TOP")
            scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.scrollToStart)
        } else {
            scrollPosition["newsfeed_items"] = Triple(0,0, ScrollIntent.standard)
        }
        _isLoading.update { false }
        _state.update {
            it.copy(
                newsItemSearchText = null,
                previousOutlineType = newsFeedItem.outlineType,
                currentKeywordBucket = if (newsFeedItem.outlineType == OutlineType.keyword) newsFeedItem.name else null,
                previousNewsFeedName = it.currentNewsFeedName,
                currentNewsFeedName = newsFeedItem.name,
                currentNewsFeedGroup = null,
                currentNewsFeedItem = newsFeedItem,
                uiMessage = null,
                uiMessageSeverity = null
            )
        }

        val syncResult = feedRepository.synchroniseReadNewsItems()
        if (syncResult is Result.Error) {
            Logger.e("WebDAV Sync failed", syncResult.throwable)
        }
    }

    private fun closeNewsItem(newsItem: NewsItem?) = viewModelScope.launch {
        newsItem?.also { ni ->
            val hideRead = _settings.value?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
            val stopWords = determineStopWords(state.value.currentNewsFeedGroup)
            _currentNewsItems.update { current -> current + (ni.uiKey to ni.copy(isRead = true)) }
            _visibleNewsItems.update { calculateVisibleNewsItems(_currentNewsItems.value.values.toList(), hideRead, stopWords) }
        }
        _state.update {
            it.copy(currentNewsItem = null)
        }
        state.value.currentNewsFeedItem?.also { nfi -> loadFeedItems(nfi) }
    }

    private fun markItemsAsRead(days: Long) = viewModelScope.launch {
        val threshold = KmpOffsetDateTime.now().minus(days.days)
        val newsItems = _currentNewsItems.value.values
            .filter { newsItem -> newsItem.updated.isBefore(threshold) }
            .map { newsItem -> newsItem.copy(isRead = true) }
        newsItems.forEach { newsItem ->
            _currentNewsItems.update { current -> current + (newsItem.uiKey to newsItem.copy(isRead = true)) }
        }
        val hideRead = _settings.value?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
        val stopWords = determineStopWords(state.value.currentNewsFeedGroup)
        _visibleNewsItems.update { calculateVisibleNewsItems(_currentNewsItems.value.values.toList(), hideRead, stopWords) }
        val markResult = feedRepository.markNewsItemsAsRead(newsItems)
        if (markResult is Result.Success) {
            _isLoading.update { false }

            val syncResult = feedRepository.synchroniseReadNewsItems()
            if (syncResult is Result.Error) {
                Logger.e("WebDAV Sync failed", syncResult.throwable)
            }
        } else if (markResult is Result.Error) {
            Logger.e("Could not load article", markResult.throwable)
            _isLoading.update { false }
            _state.update {
                it.copy(
                    uiMessage = markResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun loadArticle(newsItem: NewsItem) = viewModelScope.launch {
        val markResult = feedRepository.markNewsItemsAsRead(listOf(newsItem))
        if (markResult is Result.Success) {
            val syncResult = feedRepository.synchroniseReadNewsItems()
            if (syncResult is Result.Error) {
                Logger.e("WebDAV Sync failed", syncResult.throwable)
            }
        } else if (markResult is Result.Error) {
            Logger.e("Could not mark news item as read", markResult.throwable)
        }
        _state.update {
            it.copy(
                currentNewsItem = newsItem,
                isNewsItemSearchActive = false,
                uiMessage = null,
                uiMessageSeverity = null
            )
        }
    }

    private fun saveSettings(
        editedSettings: Settings?,
    ) = viewModelScope.launch {
        checkNotNull(editedSettings) { "No settings to save" }
        settingsRepository.setSettings(editedSettings)
            .onSuccess {
                _editedSettings.value = null
                _isLoading.update { false }
                _settings.update { editedSettings }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { error, throwable ->
                Logger.e("Could not save settings", throwable)
                _isLoading.update { false }
                _state.update {
                    it.copy(
                        isEditingSettings = false,
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
