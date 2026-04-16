package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.onError
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.repository.SettingsRepository

class NewsFeedWorker(
    private val feedRepository: FeedRepository,
    private val newsFeedConfigurationRepository: NewsFeedConfigurationRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun execute() {
        val settingsResult = settingsRepository.getSettings()
        if (settingsResult is Result.Success) {
            val settings = settingsResult.data
            val loadArticles = settings?.get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false
            val keepReadArticles = settings?.get<KeepArticlesEnum>(SK.keepReadArticles)?.longValue ?: 30
            val keepUnreadArticles = settings?.get<KeepArticlesEnum>(SK.keepUnreadArticles)?.longValue ?: 30
            val feedConfigurationResult = newsFeedConfigurationRepository.getNewsFeeds()
            if (feedConfigurationResult is Result.Success) {
                val newsFeedConfiguration = feedConfigurationResult.data
                val configurations = newsFeedConfiguration?.getNewsFeeds() ?: listOf()
                for (newsFeedConfiguration in configurations) {
                    feedRepository.refreshNewsFeed(
                        feedName = newsFeedConfiguration.name,
                        url = newsFeedConfiguration.url,
                        keepReadArticlesInDays = keepReadArticles,
                        keepUnreadArticlesInDays = keepUnreadArticles,
                        loadArticles = loadArticles
                    ) { _ -> }.onError { remote, throwable ->
                        Logger.e(
                            "Could not load feed '${newsFeedConfiguration.name}' from url '${newsFeedConfiguration.url}'",
                            throwable
                        )
                    }
                }
            } else if (feedConfigurationResult is Result.Error) {
                Logger.e("Could not load feed configuration", feedConfigurationResult.throwable)
            }
        } else if (settingsResult is Result.Error) {
            Logger.e("Could not load settings", settingsResult.throwable)
        }
    }
}
