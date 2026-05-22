package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.LogMessage.Companion.log
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
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

    private val log = Logger.withTag("NewsFeedWorker")

    suspend fun execute(maxImageSize: Int) {
        try {
            val settingsResult = settingsRepository.getSettings()
            if (settingsResult is Result.Success) {
                val settings = settingsResult.data
                val wifiOnly = settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
                val loadArticles = settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
                val keepReadArticles = settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
                val keepUnreadArticles = settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
                val feedConfigurationResult = newsFeedConfigurationRepository.getNewsFeedGroups()
                if (feedConfigurationResult is Result.Success) {
                    log(Severity.Info, "Executing news feed worker", withTag = "NHM")
                    val newsFeedGroups = feedConfigurationResult.data
                    val newsFeedConfigurations = newsFeedGroups.flatMap { nfg ->
                        nfg.newsFeeds + nfg.subGroups.flatMap { sg -> sg.newsFeeds }
                    }
                    val result = feedRepository.refreshNewsFeeds(
                        newsFeedItems = newsFeedConfigurations,
                        wifiOnly = wifiOnly,
                        keepReadArticlesInDays = keepReadArticles,
                        keepUnreadArticlesInDays = keepUnreadArticles,
                        maxImageSize = maxImageSize,
                        loadArticles = loadArticles,
                        progress = { _,_ -> }
                    )
                    if (result is Result.Success) {
                        log(Severity.Info, "News feed worker prefetching images", withTag = "NHM")
                        val (newsFeeds, changed) = result.data
                        if (changed) {
                            feedRepository.prefetchImages(
                                newsFeeds = newsFeeds
                            ) { _, _ -> }
                            settings?.also { s ->
                                val sc = s.copy(SK.feedsChanged, BooleanEnum.TRUE)
                                CoroutineScope(Dispatchers.Default).launch {
                                    settingsRepository.setSettings(sc)
                                }
                            }
                        }
                    } else if (result is Result.Error) {
                        log(Severity.Error, "Could not load news feeds", result.throwable, withTag = "NHM")
                    }
                } else if (feedConfigurationResult is Result.Error) {
                    log(Severity.Error, "Could not load feed configuration", feedConfigurationResult.throwable, withTag = "NHM")
                }
            } else if (settingsResult is Result.Error) {
                log(Severity.Error, "Could not load settings", settingsResult.throwable, withTag = "NHM")
            }
        } catch (e: Exception) {
            log(Severity.Error, "Could not execute news feed worker", e, withTag = "NHM")
        }
    }
}
