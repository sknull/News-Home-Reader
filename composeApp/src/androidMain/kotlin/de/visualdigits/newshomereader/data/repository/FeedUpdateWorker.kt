package de.visualdigits.newshomereader.data.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Responsible to update feeds while app is inactive.
 */
class FeedUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val log = kermitLogger("FeedUpdateWorker")

    private val newsFeedWorker: NewsFeedWorker by inject<NewsFeedWorker>()
    private val settingsRepository: SettingsRepository by inject<SettingsRepository>()

    init {
        log.i("#### FeedUpdateWorker initialized")
    }

    override suspend fun doWork(): Result {
        return try {
            val settingsResult = settingsRepository.getSettings()
            val maxImageSize = if (settingsResult is de.visualdigits.newshomereader.domain.model.errorhandling.Result.Success) {
                settingsResult.data?.get<Int>(SK.maxImageSize)?:1200
            } else {
                1200
            }
            newsFeedWorker.execute(maxImageSize)
            Result.success()
        } catch(e: Exception) {
            Result.retry()
        }
    }
}
