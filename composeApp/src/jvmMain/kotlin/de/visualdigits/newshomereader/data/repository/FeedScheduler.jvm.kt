package de.visualdigits.newshomereader.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual class FeedScheduler(
    private val newsFeedWorker: NewsFeedWorker,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    actual fun scheduleEvery(minutes: Long) {
        job?.cancel()
        job = scope.launch {
            while (isActive) {
                newsFeedWorker.execute()
                delay(minutes * 60 * 1000)
            }
        }
    }

    actual fun cancel() {
        job?.cancel()
    }
}
