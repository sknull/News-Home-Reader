package de.visualdigits.newshomereader.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.model.errorhandling.onError
import de.visualdigits.common.domain.model.errorhandling.onSuccess
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.error_local_wrong_filetype
import de.visualdigits.generated.AppVersion
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedItem
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
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
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
    val feedRepository: FeedRepository,
    val articleRepository: ArticleRepository,
    val settingsRepository: SettingsRepository,
    val newsFeedConfigurationRepository: NewsFeedConfigurationRepository,
    val catalogRepository: CatalogRepository,
) : ViewModel() {

    private val log = Logger.withTag("NewsHomeReaderViewModel")

    val scrollPosition: MutableMap<String, Pair<Int, Int?>> = mutableMapOf()
    var platformType: PlatformType = PlatformType.unknown

    private val _state = MutableStateFlow(NewsHomeReaderState())
    val state = _state.asStateFlow()

    init {
        log.i("Application version ${AppVersion().version} initializing...")
        loadData()
        log.i("Application started")

        _state
            .map { it.currentNewsFeedGroup to it.currentNewsFeedName }
            .distinctUntilChanged()
            .flatMapLatest { (newsFeedGroup, newsFeedName) ->
                if (newsFeedGroup != null || !newsFeedName.isNullOrBlank()) {
                    feedRepository.observeFeedItems(newsFeedGroup, newsFeedName)
                        .debounce(150.milliseconds)
                        .flowOn(Dispatchers.IO)
                        .map { items ->
                            coroutineScope {
                                // do thhe heavy lifting in the default thread
                                val enriched = items.map { newsItem ->
                                    async {
                                        val articleResult = articleRepository.getFullArticle(newsItem.id)
                                        if (articleResult is Result.Success) {
                                            newsItem.copy(newsArticle = articleResult.data)
                                        } else {
                                            newsItem
                                        }
                                    }
                                }.awaitAll()

                                // fetch current settings
                                val currentState = _state.value
                                val hideRead = currentState.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                                val newsFeedItem = currentState.currentNewsFeedItem

                                val visible = calculateVisibleNewsItems(enriched, hideRead, newsFeedItem)
                                enriched to visible
                            }
                        }
                        .flowOn(Dispatchers.Default) // do calulations on default thread
                } else {
                    flowOf(emptyList<NewsItem>() to emptyList())
                }
            }
            .onEach { (enriched, visible) ->
                _state.update {
                    it.copy(
                        currentNewsItems = enriched,
                        visibleNewsItems = visible
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onCommonAction(action: CommonAction) {
        when (action) {
            is CommonAction.OnScrollPositionChange -> {
                scrollPosition[action.id] = Pair(action.position, action.offset)
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
                _state.update {
                    it.copy(
                        originalSettings = it.settings,
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
                        Locale.setDefault(Language.valueOf(l).locale)
                    }
                }
                _state.update {
                    val settings = action.settings?.copy(
                        key = action.keyValue.descriptor.key as SK,
                        value = action.keyValue.value
                    )
                    it.copy(
                        settings = settings,
                    )
                }
            }

            is NewsHomeReaderAction.OnEditSettingsCancelClick -> {
                _state.update { state ->
                    state.originalSettings?.get<Language>(SK.language)?.also { l -> Locale.setDefault(l.locale) }
                    state.copy(
                        settings = state.originalSettings?.copy(),
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is NewsHomeReaderAction.OnSaveSettingsClick -> {
                saveSettings(action.settings)
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
                    settings.set(SK.maxImageSize, action.maxImageSize)
                    saveSettings(settings)
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
                val newsFeedConfiguration = NewsFeedConfiguration(newsFeedGroups = state.value.newsFeedGroups)
                newsFeedConfiguration.set(NC.feedName, "")
                newsFeedConfiguration.set(NC.mainGroupName, action.newsFeedGroupName)
                newsFeedConfiguration.set(NC.imageUrl, "")
                newsFeedConfiguration.set(NC.url, "")
                newsFeedConfiguration.set(NC.stopWords, "")

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
                    upsertNewsFeedItem(newsFeedItem)
                        .onSuccess { newsFeedGroups ->
                            _state.update {
                                it.copy(
                                    isAddingNewsFeedConfiguration = false,
                                    isEditingNewsFeedConfiguration = false,
                                    newsFeedGroups = newsFeedGroups?:listOf()
                                )
                            }
                        }
                        .onError { _, throwable ->
                            log.e("Could not add newsfeed item '${newsFeedItem?.name}'", throwable)
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
                            log.e("Could not add newsfeed configuration '${state.value.deleteNewsFeedItem?.name}'", throwable)
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
                    var newsFeedConfiguration = action.newsFeedConfiguration?.copy(
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
                editNewsFeedGroup(newsFeedGroup = state.value.originalNewsFeedGroup, editedNewsFeedGroupName = action.editedNewsFeedGroupName)
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
                        parentNewsFeedGroupName = action.newsFeedGroupName,
                        originalNewsFeedGroup = null,
                        currentNewsFeedGroupToDelete = null,
                        isAddingNewsFeedGroup = true
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsFeedGroupOkClick -> {
                addNewsFeedGroup(
                    parentGroupName = state.value.parentNewsFeedGroupName,
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
                loadFeedItems(action.feedName, action.currentFeedIItem)
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
                _state.update {
                    it.copy(
                        currentNewsItem = null,
                        currentNewsArticle = null,
                        isLoading = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            //
            //
            //
            is NewsHomeReaderAction.OnTabSelected -> {
                if (action.loadData) {
                    loadData()
                }
                _state.update { state ->
                    state.copy(
                        selectedTabIndex = action.selectedLabel?.let { sl -> state.tabLabels.indexOf(sl) } ?: action.index ?: 0,
                        selectedTabLabel = action.selectedLabel?:action.index?.let { i -> state.tabLabels[i] },
                        isShowInfos = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is NewsHomeReaderAction.OnCollapsibleStateChange -> {
                _state.update {
                    it.copy(
                        collapsibleState = it.collapsibleState + (action.id to action.isExpanded)
                    )
                }
            }

            is NewsHomeReaderAction.OnNewsFeedGroupCollapsibleStateChange -> {
                // keep collapsible box open when user switches from single feed to group
                _state.update {
                    val stayInGroup = !action.isExpanded && it.currentNewsFeedName != null && it.previousNewsFeedGroup == it.currentNewsFeedGroup
                    it.copy(
                        previousNewsFeedGroup = it.currentNewsFeedGroup,
                        currentNewsFeedGroup = if (action.isExpanded || stayInGroup) action.group else null,
                        clearVisibleNewsItems = if (stayInGroup) false else !action.isExpanded,
                        currentNewsFeedName = null,
                        collapsibleState = it.collapsibleState + if (stayInGroup) {
                            ("group_${action.group.name}" to true)
                        } else {
                            ("group_${action.group.name}" to action.isExpanded)
                        }
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

            is NewsHomeReaderAction.OnSearchTextChanged -> {
                _state.update {
                    it.copy(
                        searchText = action.text
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

            is NewsHomeReaderAction.OnInitializeTabs -> {
                _state.update {
                    it.copy(
                        tabLabels = action.tabLabels,
                        selectedTabIndex = 0,
                        selectedTabLabel = action.tabLabels.firstOrNull(),
                        isShowInfos = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }

            is NewsHomeReaderAction.OnBusyOkClick -> {
                _state.update {
                    it.copy(
                        isEditingSettings = false,
                        isLoading = false,
                        logs = listOf()
                    )
                }
            }
        }
    }

    private fun addNewsFeedGroup(
        parentGroupName: String?,
        newsFeedGroupName: String
    ) = viewModelScope.launch {
        val addResult = newsFeedConfigurationRepository.upsertNewsFeedGroupSingle(
            NewsFeedGroup(
                parentGroupName = parentGroupName,
                name = newsFeedGroupName
            )
        )
        when (addResult) {
            is Result.Success -> {
                addResult.data
            }

            is Result.Error -> {
                log.e("Could not add newsfeed group '$newsFeedGroupName'", addResult.throwable)
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
                    log.e("Could not add root group '${mainCategory.name}'", addResult.throwable)
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
                    log.e("Could not add root group '${mainCategory?.name}'", addResult.throwable)
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
                                isLoading = false,
                                newsFeedGroups = newsFeedGroups
                            )
                        }
                    }.onError { error, throwable ->
                        log.e("Could not get newsfeed groups", throwable)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
                } else if (result is Result.Error) {
                    log.e("Could not upsert news feed configuration", result.throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
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
                            isLoading = false,
                            newsFeedGroups = newsFeedGroups ?: listOf()
                        )
                    }
                }
                .onError { error, throwable ->
                    log.e("Could not delete feed configuration", throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        }
    }

    private suspend fun addGroup(
        newsFeedGroup: NewsFeedGroup
    ): Result<NewsFeedGroup?, DataError.Local> {
log.i("add group '${newsFeedGroup.parentGroupName}/${newsFeedGroup.name}'")
        val getResult = newsFeedConfigurationRepository.getNewsFeedGroupByName(newsFeedGroup.parentGroupName, newsFeedGroup.name)
        return if (getResult is Result.Success) {
            if(getResult.data == null) {
                newsFeedConfigurationRepository.upsertNewsFeedGroupSingle(newsFeedGroup)
            } else {
                Result.Success(getResult.data)
            }
        } else if (getResult is Result.Error) {
            log.e("Could not get root category '${newsFeedGroup.name}'", getResult.throwable)
            Result.Error(DataError.Local.UNKNOWN, getResult.throwable)
        } else {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    private fun editNewsFeedGroup(newsFeedGroup: NewsFeedGroup?, editedNewsFeedGroupName: String) = viewModelScope.launch {
        val editResult = newsFeedConfigurationRepository.editNewsFeedGroup(newsFeedGroup, editedNewsFeedGroupName)
        if (editResult is Result.Success) {
            _state.update {
                it.copy(
                    isEditingNewsFeedGroup = false,
                    newsFeedGroups = editResult.data
                )
            }
        } else if (editResult is Result.Error) {
            log.e("Could not get old newsfeed group '${newsFeedGroup?.name}'", editResult.throwable)
            _state.update {
                it.copy(
                    isEditingNewsFeedGroup = false,
                    uiMessage = editResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private suspend fun upsertNewsFeedItem(newsFeedItem: NewsFeedItem?): Result<List<NewsFeedGroup>?, DataError.Local> {
        return newsFeedItem
            ?.let { nfc -> newsFeedConfigurationRepository.upsertNewsFeedItem(nfc) }
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
                                newsFeedItem = newEntity
                            )
                        )
                    }
                }
                .onError { _, throwable ->
                    log.e("Could not modify newsfeed configuration from '${oldEntity.name}' to '${newEntity.name}'", throwable)
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
                log.e("Could not add newsfeed group '${newsFeedGroup.name}'", deleteResult.throwable)
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
        _state.update {
            it.copy(
                isLoading = true,
            )
        }
        scrollPosition["newsfeed_$feedName"] = Pair(0, 0)
        val wifiOnly = state.value.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        val loadArticles = state.value.settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue?:false
        val keepReadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue?:30
        val keepUnreadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue?:30
        val maxImageSize = state.value.settings?.get<Int>(SK.maxImageSize)?:1200
        feedName?.also { fn ->
            url?.also { u ->
                val feedResult = feedRepository.refreshNewsFeed(
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
                                newsFeedItem = it.currentNewsFeedItem
                            ),
                            isLoading = false,
                            uiMessage = null,
                            uiMessageSeverity = null
                        )
                    }
                    prefetchImages(changed, listOfNotNull(newsFeed))
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    currentProgress = 0.0f,
                                    progressStage = ProgressStage.NONE,
                                )
                            }
                        }
                        .onError { _, throwable ->
                            log.e("Could not prefetch images", throwable)
                        }
                } else if (feedResult is Result.Error) {
                    log.e("Could not load feed '$feedName'", feedResult.throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
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
        val result = newsFeedConfigurationRepository.getNewsFeedGroups()
        if (result is Result.Success) {
            val newsFeedGroups = result.data
            val newsFeedConfigurations = newsFeedGroups.flatMap { nfg ->
                nfg.newsFeeds + nfg.subGroups.flatMap { sg -> sg.newsFeeds}
            }
            val newsFeedResult = refreshNewsFeeds(newsFeedConfigurations)
            if (newsFeedResult is Result.Success) {
                val (newsFeeds, changed) = newsFeedResult.data
                _state.update {
                    val currentNewsItems = newsFeeds.find { nf -> nf.feedName == it.currentNewsFeedName }?.items ?: listOf()
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        isEditingSettings = false,
                        currentNewsItems = currentNewsItems,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = currentNewsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                            newsFeedItem = it.currentNewsFeedItem
                        ),
                        newsFeedGroups = newsFeedGroups
                    )
                }
                prefetchImages(changed, newsFeeds)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                currentProgress = 0.0f,
                                progressStage = ProgressStage.NONE,
                            )
                        }
                    }
                    .onError { _, throwable ->
                        log.e("Could not prefetch images", throwable)
                    }
            } else if (newsFeedResult is Result.Error) {
                log.e("Could not refresh newsfeeds'", newsFeedResult.throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                    )
                }
            }
        }
    }

    private fun importSettings(fileName: String, ins: InputStream) = viewModelScope.launch {
        log.i("Importing settings")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            settingsRepository.importSettings(ins)
                .onSuccess { settings ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            settings = settings,
                            isEditingSettings = false,
                            uiMessage = null,
                        )
                    }
                }
                .onError { error, throwable ->
                    log.e("Could not import settings", throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportSettings(fileName: String, outs: OutputStream) = viewModelScope.launch {
        log.i("Exporting settings")
        if (fileName.endsWith(".json", ignoreCase = true)) {
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            val settings = state.value.settings
            if(settings != null) {
                settingsRepository.exportSettings(settings, outs)
                    .onSuccess {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                uiMessage = null,
                            )
                        }
                    }
                    .onError { error, throwable ->
                        log.e("Could not export settings", throwable)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                uiMessage = error.toUiText(),
                                uiMessageSeverity = Severity.Error
                            )
                        }
                    }
            }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun importOpml(fileName: String, ins: InputStream) = viewModelScope.launch {
        log.i("Importing opml...")
        if (fileName.endsWith(".opml", ignoreCase = true)) {
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            val newFeedConfigurationResult = newsFeedConfigurationRepository.setNewsFeedGroups(ins)
            if (newFeedConfigurationResult is Result.Success) {
                val feedResult = newsFeedConfigurationRepository.getNewsFeedGroups()
                val newsFeedGroups = if (feedResult is Result.Success) {
                    feedResult.data
                } else {
                    state.value.newsFeedGroups
                }
                val newsFeedConfigurations = newsFeedGroups.flatMap { nfg -> nfg.newsFeeds }
                val newsFeedResult = refreshNewsFeeds(newsFeedConfigurations)
                if (newsFeedResult is Result.Success) {
                    val (newsFeeds, changed) = newsFeedResult.data
                    _state.update {
                        val currentNewsItems = newsFeeds.find { nf -> nf.feedName == it.currentNewsFeedName }?.items ?: listOf()
                        it.copy(
                            isLoading = false,
                            currentProgress = 0.0f,
                            progressStage = ProgressStage.NONE,
                            isEditingSettings = false,
                            currentNewsItems = currentNewsItems,
                            visibleNewsItems = calculateVisibleNewsItems(
                                newsItems = currentNewsItems,
                                hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
                                newsFeedItem = it.currentNewsFeedItem
                            ),
                            newsFeedGroups = newsFeedGroups
                        )
                    }
                    prefetchImages(changed, newsFeeds)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    currentProgress = 0.0f,
                                    progressStage = ProgressStage.NONE,
                                    uiMessage = null,
                                )
                            }
                        }
                        .onError { _, throwable ->
                            log.e("Could not prefetch images", throwable)
                        }
                } else if (newsFeedResult is Result.Error) {
                    log.e("Could not import OPML", newsFeedResult.throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentProgress = 0.0f,
                            progressStage = ProgressStage.NONE,
                            uiMessage = newsFeedResult.error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
            } else if (newFeedConfigurationResult is Result.Error){
                log.e("Could not import OPML", newFeedConfigurationResult.throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
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
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun exportOpml(fileName: String, outs: OutputStream) = viewModelScope.launch {
        log.i("Exporting opml...")
        if (fileName.endsWith(".opml", ignoreCase = true)) {
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            newsFeedConfigurationRepository.saveNewsFeedGroups(outs)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            uiMessage = null,
                        )
                    }
                }
                .onError { error, throwable ->
                    log.e("Could not export opml", throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            uiMessage = error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = UiText.StringResourceId(Res.string.error_local_wrong_filetype),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private suspend fun refreshNewsFeeds(newsFeedItems: List<NewsFeedItem>): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> {
        val wifiOnly = state.value.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        val loadArticles = state.value.settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
        val keepReadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
        val keepUnreadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
        val maxImageSize = state.value.settings?.get<Int>(SK.maxImageSize) ?: 1200

        scrollPosition
            .keys
            .filter { k -> k.startsWith("newsfeed_") }
            .forEach { k -> scrollPosition[k] = Pair(0, 0)}

        return feedRepository.refreshNewsFeeds(
            newsFeedItems = newsFeedItems,
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
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE
                    )
                }
            }
            .onError { error, throwable ->
                log.e("Could not refresh images", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
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
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        catalogRepository.loadCatalog()
            .onSuccess { newsFeedCatalog ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isViewingCatalog = isExpanded,
                        newsFeedCatalog = newsFeedCatalog,
                        filteredCatalog = newsFeedCatalog
                    )
                }
            }
            .onError { _, throwable ->
                log.e("Could not log catalog", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
    }

    private fun loadData() = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
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
                val newSettings = Settings()
                newSettings.set(SK.displayTheme, DisplayThemeEnum.LIGHT)
                newSettings.set(SK.language, Language.EN)
                newSettings.set(SK.refreshInterval, RefreshIntervalEnum.MINUTES_60)
                newSettings.set(SK.refreshWifiOnly, BooleanEnum.TRUE)
                newSettings.set(SK.maxImageSize, 1200)
                newSettings.set(SK.loadArticles, BooleanEnum.FALSE)
                newSettings.set(SK.hideRead, BooleanEnum.TRUE)
                newSettings.set(SK.keepReadArticles, KeepArticlesEnum.DAYS_3)
                newSettings.set(SK.keepUnreadArticles, KeepArticlesEnum.DAYS_7)
                settingsRepository.setSettings(newSettings)
                    .onError { _, throwable ->
                        log.e("Could not safe initial settings", throwable)
                    }
                newSettings
            }

            Locale.setDefault(finalSettings.get<Language>(SK.language)?.locale?: Language.EN.locale)

            _state.update {
                it.copy(
                    settings = finalSettings,
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = null,
                    uiMessageSeverity = null,
                    collapsibleState = mapOf("group_newsfeeds_navigation" to true)
                )
            }
        } else if (result is Result.Error) {
            log.e("Could not load data", result.throwable)
            _state.update {
                it.copy(
                    isLoading = false,
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
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { _, throwable ->
                log.e("Could not get settings", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
    }

    private fun loadFeedItems(
        feedName: String?,
        currentFeedItem: NewsFeedItem
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                currentNewsFeedName = feedName,
                currentNewsFeedGroup = null,
                currentNewsFeedItem = currentFeedItem,
                currentNewsArticle = null,
                isLoading = false,
                currentProgress = 0.0f,
                progressStage = ProgressStage.NONE,
                uiMessage = null,
                uiMessageSeverity = null
            )
        }
    }

    private fun markItemsAsRead(days: Long) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
            )
        }
        val threshold = OffsetDateTime.now().minus(days, ChronoUnit.DAYS)
        val newsItems = state.value.currentNewsItems
            .filter { newsItem -> newsItem.updated.isBefore(threshold) }
            .map { newsItem -> newsItem.copy(isRead = true) }
        feedRepository.markNewsItemsAsRead(newsItems.map { newsItem -> newsItem.id })
            .onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
                        clearVisibleNewsItems = days == 0L,
                        progressStage = ProgressStage.NONE,
                        currentNewsItems = newsItems,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = newsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false,
                            newsFeedItem = it.currentNewsFeedItem
                        ),
                    )
                }
            }
            .onError { error, throwable ->
                log.e("Could not load article", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        uiMessage = error.toUiText(),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
    }

    private fun loadArticle(newsItem: NewsItem) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
            )
        }
        var copy = newsItem.copy(isRead = true)
        feedRepository.upsertNewsItem(copy, true)
        val articleResult = articleRepository.readFullArticle(newsItem)
        if (articleResult is Result.Success) {
            copy = copy.copy(newsArticle = articleResult.data.first)
            _state.update {
                val currentNewsItems = it.currentNewsItems.map { ni ->
                    if (ni.id == newsItem.id) copy else ni
                }
                it.copy(
                    currentNewsItem = copy,
                    currentNewsArticle = articleResult.data.first,
                    currentNewsItems = currentNewsItems,
                    visibleNewsItems = calculateVisibleNewsItems(
                        newsItems = currentNewsItems,
                        hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false,
                        newsFeedItem = it.currentNewsFeedItem
                    ),
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = null,
                    uiMessageSeverity = null
                )
            }
        } else if (articleResult is Result.Error) {
            log.e("Could not load article", articleResult.throwable)
            _state.update {
                it.copy(
                    isLoading = false,
                    currentProgress = 0.0f,
                    progressStage = ProgressStage.NONE,
                    uiMessage = articleResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }

    private fun saveSettings(
        settings: Settings,
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
            )
        }

        settingsRepository.setSettings(settings)
            .onSuccess {
                _state.update {
                    it.copy(
                        settings = settings.copy(),
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = it.currentNewsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false,
                            newsFeedItem =it.currentNewsFeedItem
                        ),
                        isLoading = false,
                        currentProgress = 0.0f,
                        progressStage = ProgressStage.NONE,
                        isEditingSettings = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
            .onError { error, throwable ->
                log.e("Could not save settings", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
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

    private fun calculateVisibleNewsItems(newsItems: List<NewsItem>, hideRead: Boolean, newsFeedItem: NewsFeedItem?): List<NewsItem> {
        val stopWords = newsFeedItem?.stopWords?:listOf()
        val sortedByDescending = newsItems
            .filter { item -> (!hideRead || !item.isRead)
                    && item.title.nostop(stopWords)
                    && item.summary.nostop(stopWords)
            }
            .sortedByDescending { item -> item.updated }
        return sortedByDescending
    }
}

private fun String.nostop(stopWords: List<String>): Boolean = stopWords.none { w -> this.contains(w, ignoreCase = true) }
