package de.visualdigits.newshomereader.data.repository

expect class FeedScheduler {

    fun scheduleEvery(minutes: Long)

    fun cancel()
}
