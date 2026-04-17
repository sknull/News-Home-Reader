package de.visualdigits.newshomereader.data.repository

/**
 * Responsible to refresh feeds while app is active.
 */
expect class FeedScheduler {

    fun scheduleEvery(minutes: Long, maxImageSize: Int)

    fun cancel()
}
