package de.visualdigits.newshomereader.presentation.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.generated.AppVersion
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.errorhandling.onSuccess
import de.visualdigits.newshomereader.domain.model.errorhandling.toUiText
import de.visualdigits.newshomereader.domain.model.platform.PlatformType
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.model.unified.NewsFeed
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
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
) : ViewModel() {

    private val log = kermitLogger(this::class)

    val scrollPosition: MutableMap<String, Pair<Int, Int?>> = mutableMapOf()
    var platformType: PlatformType = PlatformType.unknown

    private val _state = MutableStateFlow(NewsHomeReaderState())
    val state = _state.asStateFlow()

    init {
        log.i("#### Application version ${AppVersion().version} initializing...")
        loadData()
        log.i("#### Application started")

        _state
            .map { it.currentFeedName }
            .distinctUntilChanged()
            .flatMapLatest { feedName ->
                if (!feedName.isNullOrBlank()) {
                    feedRepository.observeFeedItems(feedName)
                        .debounce(150.milliseconds)
                        .map { items ->
                            // ALLES im Hintergrund vorbereiten
                            withContext(Dispatchers.Default) {
                                val enriched = items.map { newsItem ->
                                    val articleResult = articleRepository.getFullArticle(newsItem.id)
                                    if (articleResult is Result.Success) {
                                        newsItem.copy(newsArticle = articleResult.data)
                                    } else {
                                        newsItem
                                    }
                                }

                                // Auch das Filtern/Sichtbarkeit im Hintergrund berechnen
                                val hideRead = _state.value.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                                val visible = calculateVisibleNewsItems(enriched, hideRead)

                                // Paar aus beidem zurückgeben
                                enriched to visible
                            }
                        }
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
                importOpml(action.ins)
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
                _state.update {
                    it.copy(
                        isEditingNewsFeedConfiguration = true,
                        originalNewsFeedConfiguration = action.originalNewsFeedConfiguration
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsFeedConfigurationClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = true
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
                        isDeletingNewsFeedConfiguration = true
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsFeedConfigurationOkClick -> {
                _state.update {
                    it.copy(
                        isAddingNewsFeedConfiguration = false,
                        isEditingNewsFeedConfiguration = false,
                        isDeletingNewsFeedConfiguration = false
                    )
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

            //
            // NewsFeedGroup
            //
            is NewsHomeReaderAction.OnEditNewsfeedGroupGroupClick -> {
                _state.update {
                    it.copy(
                        isEditingNewsFeedGroup = true,
                        originalNewsFeedGroupName = action.originalNewsFeedGroupName,
                        currentNewsFeedGroupName = action.originalNewsFeedGroupName,
                    )
                }
            }
            is NewsHomeReaderAction.OnEditNewsFeedGroupOkClick -> {
                editNewsFeedGroup(state.value.originalNewsFeedGroupName?:"", action.newsFeedGroupName)
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
                        originalNewsFeedGroupName = null,
                        currentNewsFeedGroupName = null,
                        isAddingNewsFeedGroup = true
                    )
                }
            }
            is NewsHomeReaderAction.OnAddNewsFeedGroupOkClick -> {
                addNewsFeedGroup(action.newsFeedGroupName)
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
                        currentNewsFeedGroupName = null
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsfeedGroupClick -> {
                _state.update {
                    it.copy(
                        isDeletingNewsFeedGroup = true,
                        currentNewsFeedGroupName = action.newsFeedGroupName
                    )
                }
            }
            is NewsHomeReaderAction.OnDeleteNewsfeedGroupOkClick -> {
                deleteNewsFeedGroup(state.value.currentNewsFeedGroupName)
            }

            //
            // News
            //
            is NewsHomeReaderAction.OnNewsFeedRefresh -> {
                refreshFeed(action.feedName, action.url)
            }

            is NewsHomeReaderAction.OnNewsFeedsRefresh -> {
                refreshNewsFeeds()
            }

            is NewsHomeReaderAction.OnNewsFeedClicked -> {
                loadFeedItems(action.feedName, action.currentFeedConfiguration)
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

            is NewsHomeReaderAction.OnScrollPositionChange -> {
                scrollPosition[action.id] = Pair(action.position, action.offset)
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
                        isConverting = false,
                        logs = listOf()
                    )
                }
            }
        }
    }

    private fun addNewsFeedGroup(newsFeedGroupName: String) = viewModelScope.launch {
        val addResult = newsFeedConfigurationRepository.upsertNewsFeedGroup(NewsFeedGroup(name = newsFeedGroupName))
            if (addResult is Result.Success) {
                _state.update {
                    it.copy(
                        isAddingNewsFeedGroup = false,
                        newsFeedGroups = addResult.data
                    )
                }
            } else if (addResult is Result.Error) {
                log.e("Could not add newsfeed group '$newsFeedGroupName'")
            }
    }

    private fun editNewsFeedGroup(oldFeedGroupName: String, newNewsFeedGroupName: String) = viewModelScope.launch {
        val getResult = newsFeedConfigurationRepository.getNewsFeedGroupByName(oldFeedGroupName)
        if (getResult is Result.Success) {
            val newsFeedGroup = getResult.data?.copy(name = newNewsFeedGroupName)?: NewsFeedGroup(name = newNewsFeedGroupName)
            val upsertResult = newsFeedConfigurationRepository.upsertNewsFeedGroup(newsFeedGroup)
            if (upsertResult is Result.Success) {
                _state.update {
                    it.copy(
                        isEditingNewsFeedGroup = false,
                        newsFeedGroups = upsertResult.data
                    )
                }
            } else if (upsertResult is Result.Error) {
                log.e("Could not upsertResult newsfeed group '$newNewsFeedGroupName'")
            }
        } else if (getResult is Result.Error) {
            log.e("Could not get old newsfeed group '$oldFeedGroupName'")
        }
    }


    private fun deleteNewsFeedGroup(newsFeedGroupName: String?) = viewModelScope.launch {
        if (newsFeedGroupName != null) {
            val deleteResult = newsFeedConfigurationRepository.deleteNewsFeedGroup(newsFeedGroupName)
            if (deleteResult is Result.Success) {
                _state.update {
                    it.copy(
                        isDeletingNewsFeedGroup = false,
                        currentNewsArticle = null,
                        newsFeedGroups = deleteResult.data
                    )
                }
            } else if (deleteResult is Result.Error) {
                log.e("Could not add newsfeed group '$newsFeedGroupName'")
            }
        }
    }

    private fun refreshNewsFeeds() = viewModelScope.launch {
        val result = newsFeedConfigurationRepository.getNewsFeedGroups()
        if (result is Result.Success) {
            val newsFeedGroups = result.data
            val newsFeedConfigurations = newsFeedGroups.flatMap { nfg -> nfg.newsFeeds }
            refreshNewsFeeds(newsFeedConfigurations)
        }
    }

    private fun importOpml(ins: InputStream) = viewModelScope.launch {
        log.i("#### Importing opml...")
        _state.update {
            it.copy(
                isLoading = true,
            )
        }

        val newFeedConfigurationResult = newsFeedConfigurationRepository.setNewsFeedGroups(ins)
        if (newFeedConfigurationResult is Result.Success) {
            val newsFeedGroups = newFeedConfigurationResult.data
            val newsFeedConfigurations = newsFeedGroups.flatMap { nfg -> nfg.newsFeeds }
            val newsFeedResult = refreshNewsFeeds(newsFeedConfigurations)

            if (newsFeedResult is Result.Success) {
                val (newsFeeds, _) = newsFeedResult.data
                _state.update {
                    val currentNewsItems = newsFeeds.find { nf -> nf.feedName == it.currentFeedName }?.items ?: listOf()
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
                        isEditingSettings = false,
                        currentNewsItem = null,
                        currentNewsArticle = null,
                        currentNewsItems = currentNewsItems,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = currentNewsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                        ),
                        newsFeedGroups = newsFeedGroups
                    )
                }
            } else if (newsFeedResult is Result.Error) {
                log.e("Could not import OPML", newsFeedResult.throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentProgress = 0.0f,
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
                    uiMessage = newFeedConfigurationResult.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }
    }


    private suspend fun refreshNewsFeeds(newsFeedConfigurations: List<NewsFeedConfiguration>): Result<Pair<List<NewsFeed>, Boolean>, DataError.Remote> {
        val wifiOnly = state.value.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        val loadArticles = state.value.settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
        val keepReadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
        val keepUnreadArticles = state.value.settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
        val maxImageSize = state.value.settings?.get<Int>(SK.maxImageSize) ?: 1200

        return feedRepository.refreshNewsFeeds(
            newsFeedConfigurations = newsFeedConfigurations,
            wifiOnly = wifiOnly,
            keepReadArticlesInDays = keepReadArticles,
            keepUnreadArticlesInDays = keepUnreadArticles,
            maxImageSize = maxImageSize,
            loadArticles = loadArticles,
            progress = { p ->
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            currentProgress = p,
                        )
                    }
                }
            }
        )
    }

    private fun refreshFeed(
        feedName: String?,
        url: String?
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
            )
        }
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
                ) { p ->
                    viewModelScope.launch {
                        _state.update {
                            it.copy(
                                currentProgress = p,
                            )
                        }
                    }
                }
                if (feedResult is Result.Success) {
                    val (newsFeed, _) = feedResult.data

                    _state.update {
                        it.copy(
                            currentFeedName = fn,
                            currentProgress = 0.0f,
                            currentNewsItems = newsFeed?.items?.toList()?:listOf(), // force repaint
                            visibleNewsItems = calculateVisibleNewsItems(
                                newsItems = newsFeed?.items ?: listOf(),
                                hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false
                            ),
                            currentNewsArticle = null,
                            isLoading = false,
                            uiMessage = null,
                            uiMessageSeverity = null
                        )
                    }
                } else if (feedResult is Result.Error) {
                    log.e("Could not load feed '$feedName'", feedResult.throwable)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            currentProgress = 0.0f,
                            uiMessage = feedResult.error.toUiText(),
                            uiMessageSeverity = Severity.Error
                        )
                    }
                }
            }
        }
    }

    private fun loadData() = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
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
                    uiMessage = result.error.toUiText(),
                    uiMessageSeverity = Severity.Error
                )
            }
        }

        newsFeedConfigurationRepository.getNewsFeedGroups()
            .onSuccess { newsFeedConfiguration ->
                _state.update {
                    it.copy(
                        newsFeedGroups = newsFeedConfiguration,
                        isLoading = false,
                        uiMessage = null,
                        uiMessageSeverity = null
                    )
                }
            }
    }

    private fun loadFeedItems(
        feedName: String,
        currentFeedConfiguration: NewsFeedConfiguration
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                currentFeedName = feedName,
                currentFeedConfiguration = currentFeedConfiguration,
                currentNewsArticle = null,
                isLoading = false,
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
                        currentNewsItems = newsItems,
                        visibleNewsItems = calculateVisibleNewsItems(
                            newsItems = newsItems,
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false
                        ),
                    )
                }
            }
            .onError { error, throwable ->
                log.e("Could not load article", throwable)
                _state.update {
                    it.copy(
                        isLoading = false,
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
                        hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false
                    ),
                    isLoading = false,
                    uiMessage = null,
                    uiMessageSeverity = null
                )
            }
        } else if (articleResult is Result.Error) {
            log.e("Could not load article", articleResult.throwable)
            _state.update {
                it.copy(
                    isLoading = false,
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
                            hideRead = it.settings?.get<BooleanEnum>(SK.hideRead)?.booleanValue?:false
                        ),
                        isLoading = false,
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
                        uiMessage = error.toUiText(),
                        uiMessageSeverity = Severity.Error
                    )
                }
            }
    }

    private fun calculateVisibleNewsItems(newsItems: List<NewsItem>, hideRead: Boolean): List<NewsItem> {
        val sortedByDescending = newsItems
            .filter { item -> !hideRead || !item.isRead }
            .sortedByDescending { item -> item.updated }
        return sortedByDescending
    }
}
