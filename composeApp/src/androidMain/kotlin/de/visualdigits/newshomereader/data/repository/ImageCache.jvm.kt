package de.visualdigits.newshomereader.data.repository

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import okio.Path.Companion.toPath

actual class ImageCache(
    private val context: Context
) {

    private val sharedImageLoader by lazy {
        createImageLoader(
            context = context,
            cacheDirectory = context.cacheDir.resolve("image_cache").absolutePath.toPath()
        )
    }

    actual fun getImageLoader(): ImageLoader = sharedImageLoader

    actual fun prefetchImages(urls: List<String>) {
        urls.forEach { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()
            sharedImageLoader.enqueue(request)
        }
    }
}
