package de.visualdigits.newshomereader.data.repository
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

actual class FeedScheduler(
    private val context: Context
) {

    actual fun scheduleEvery(minutes: Long) {
        val workRequest = PeriodicWorkRequestBuilder<FeedUpdateWorker>(
            repeatInterval = minutes,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "feed_update",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    actual fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork("feed_update")
    }
}

