package de.visualdigits.newshomereader.data.repository

import coil3.ImageLoader

expect class ImageCache {

    fun getImageLoader(): ImageLoader

    suspend fun prefetchImages(
        urls: List<String>,
        onImageDone: suspend () -> Unit
    )
}
