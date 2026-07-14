package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.opml.OutlineType
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsFeedWorker(
    private val connectivityManager: ConnectivityManager,
    private val feedRepository: FeedRepository,
    private val newsFeedConfigurationRepository: NewsFeedConfigurationRepository,
    private val settingsRepository: SettingsRepository,
) {

    suspend fun execute(maxImageSize: Int) {
        try {
            val settingsResult = settingsRepository.getSettings()
            if (settingsResult is Result.Success) {
                val settings = settingsResult.data
                val wifiOnly = settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
                val loadArticles = settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
                val prefetchImages = settings?.get<BooleanEnum>(SK.prefetchImages)?.booleanValue ?: false
                val keepReadArticles = settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
                val keepUnreadArticles = settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
                val feedConfigurationResult = newsFeedConfigurationRepository.getNewsFeedGroups()
                if (feedConfigurationResult is Result.Success) {
                    Logger.i("Executing news feed worker")
                    val newsFeedGroups = feedConfigurationResult.data
                    val newsFeedConfigurations = newsFeedGroups.flatMap { nfg ->
                        nfg.newsFeeds + nfg.subGroups.flatMap { sg -> sg.newsFeeds }
                    }.filter { nfi -> nfi.outlineType != OutlineType.keyword }
                    val result = if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                        val newsFeedsResult = feedRepository.refreshNewsFeeds(
                            newsFeedItems = newsFeedConfigurations,
                            progress = { _,_ -> }
                        )
                        if (newsFeedsResult is Result.Success) {
                            val (newsFeeds, newsItems) = newsFeedsResult.data
                            feedRepository.refreshNewsFeedItems(
                                newsFeeds = newsFeeds,
                                newsItems = newsItems,
                                wifiOnly = wifiOnly,
                                keepReadArticlesInDays = keepReadArticles,
                                keepUnreadArticlesInDays = keepUnreadArticles,
                                maxImageSize = maxImageSize,
                                loadArticles = loadArticles,
                                progress = { _,_ -> }
                            )
                        } else if (newsFeedsResult is Result.Error) {
                            Logger.e("Could not refresh news feeds", newsFeedsResult.throwable)
                            feedRepository.getAllNewsFeeds()
                        } else {
                            Logger.i("Could not get news feeds from remote - fetching newsFeeds from database")
                            feedRepository.getAllNewsFeeds()
                        }
                    } else {
                        Logger.i("No free of charge internet connection available - fetching newsFeeds from database")
                        feedRepository.getAllNewsFeeds()
                    }
                    if (result is Result.Success) {
                        if (prefetchImages) {
                            Logger.i("News feed worker prefetching images")
                            val (newsFeeds, changed) = result.data
                            if (changed) {
                                feedRepository.prefetchImages(
                                    newsFeeds = newsFeeds
                                ) { _, _ -> }
                                val sc = settings.copy(SK.feedsChanged, BooleanEnum.TRUE)
                                CoroutineScope(Dispatchers.Default).launch {
                                    settingsRepository.setSettings(sc)
                                }
                            }
                        }
                    } else if (result is Result.Error) {
                        Logger.e("Could not load news feeds", result.throwable)
                    }
                } else if (feedConfigurationResult is Result.Error) {
                    Logger.e("Could not load feed configuration", feedConfigurationResult.throwable)
                }
            } else if (settingsResult is Result.Error) {
                Logger.e("Could not load settings", settingsResult.throwable)
            }
        } catch (e: Exception) {
            Logger.e("Could not execute news feed worker", e)
        }
    }
}
