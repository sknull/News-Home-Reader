package de.visualdigits.newshomereader.data.repository

import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.errorhandling.onSuccess
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsFeedWorker(
    private val feedRepository: FeedRepository,
    private val newsFeedConfigurationRepository: NewsFeedConfigurationRepository,
    private val settingsRepository: SettingsRepository,
) {

    private val log = kermitLogger(this::class)

    suspend fun execute(maxImageSize: Int) {
        val settingsResult = settingsRepository.getSettings()
        if (settingsResult is Result.Success) {
            val settings = settingsResult.data
            val wifiOnly = settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
            val loadArticles = settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
            val keepReadArticles = settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
            val keepUnreadArticles = settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
            val feedConfigurationResult = newsFeedConfigurationRepository.getNewsFeedGroups()
            if (feedConfigurationResult is Result.Success) {
                val newsFeedGroups = feedConfigurationResult.data
                val newsFeedConfigurations = newsFeedGroups.flatMap { nfg -> nfg.newsFeeds }
                feedRepository.refreshNewsFeeds(
                    newsFeedConfigurations = newsFeedConfigurations,
                    wifiOnly = wifiOnly,
                    keepReadArticlesInDays = keepReadArticles,
                    keepUnreadArticlesInDays = keepUnreadArticles,
                    maxImageSize = maxImageSize,
                    loadArticles = loadArticles,
                    progress = {}
                ).onSuccess { result ->
                    if (result.second) {
                        settings?.also { s ->
                            s.set(SK.feedsChanged, BooleanEnum.TRUE)
                            CoroutineScope(Dispatchers.Default).launch {
                                settingsRepository.setSettings(s)
                            }
                        }
                    }
                }.onError { _, throwable ->
                    log.e("Could not load news feeds", throwable)
                }
            } else if (feedConfigurationResult is Result.Error) {
                log.e("Could not load feed configuration", feedConfigurationResult.throwable)
            }
        } else if (settingsResult is Result.Error) {
            log.e("Could not load settings", settingsResult.throwable)
        }
    }
}
