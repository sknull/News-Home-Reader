package de.visualdigits.newshomereader.data.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import io.ktor.utils.io.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Responsible to update feeds while app is inactive.
 */
class FeedUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val newsFeedWorker: NewsFeedWorker by inject<NewsFeedWorker>()
    private val settingsRepository: SettingsRepository by inject<SettingsRepository>()

    init {
        Logger.i("FeedUpdateWorker initialized")
    }

    override suspend fun doWork(): Result {
        return try {
            val settingsResult = settingsRepository.getSettings()
            val maxImageSize = if (settingsResult is de.visualdigits.common.domain.model.errorhandling.Result.Success) {
                settingsResult.data?.get<Int>(SK.maxImageSize) ?: 1200
            } else {
                1200
            }

            // Regelmäßige Prüfung, ob der Worker in der Zwischenzeit gecancelt wurde
            if (isStopped) {
                Logger.i("Update worker was stopped - exiting")
                return Result.failure()
            }

            newsFeedWorker.execute(maxImageSize)
            Result.success()
        } catch (e: CancellationException) {
            // 🟢 DAS RETTET DIE APP: Reiche den Abbruch ungestört an den WorkManager weiter!
            Logger.i("FeedUpdateWorker was successfully cancelled by the application.")
            throw e
        } catch (e: Exception) {
            // Nur echte Fehler (Netzwerkausfall, DB-Fehler) führen zu einem Retry
            Logger.e("Something went wrong during refresh news feed", e)
            Result.retry()
        }
    }
}
