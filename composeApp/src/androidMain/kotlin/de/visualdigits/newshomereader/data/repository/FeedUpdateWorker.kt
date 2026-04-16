package de.visualdigits.newshomereader.data.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger

class FeedUpdateWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val newsFeedWorker: NewsFeedWorker,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            newsFeedWorker.execute()
            Result.success()
        } catch(e: Exception) {
            Logger.Companion.e("Could not refresh repositories", e)
            Result.retry()
        }
    }
}
