package de.visualdigits.newshomereader.data.repository

import coil3.ImageLoader

expect class ImageCache {

    fun getImageLoader(): ImageLoader

    fun prefetchImages(urls: List<String>)
}
