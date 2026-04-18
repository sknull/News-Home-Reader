package de.visualdigits.newshomereader.data.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.SettingsRepository

/**
 * Responsible to update feeds while app is inactive.
 */
class FeedUpdateWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val newsFeedWorker: NewsFeedWorker,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {

    private val log = kermitLogger()

    override suspend fun doWork(): Result {
        return try {
            val settingsResult = settingsRepository.getSettings()
            val maxImageSize = if (settingsResult is de.visualdigits.newshomereader.domain.model.errorhandling.Result.Success) {
                settingsResult.data?.get<Int>(SK.maxImageSize)?:1200
            } else {
                1200
            }
            log.i("Running scheduled newsfeed refresh...")
            newsFeedWorker.execute(maxImageSize)
            Result.success()
        } catch(e: Exception) {
            log.e("Could not refresh repositories", e)
            Result.retry()
        }
    }
}
