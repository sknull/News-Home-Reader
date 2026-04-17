package de.visualdigits.newshomereader.data.repository

import android.content.Context
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.bitmapConfig
import io.ktor.client.HttpClient
import okio.Path.Companion.toPath

actual class ImageCache(
    private val context: Context,
    private val httpClient: HttpClient
) {

    private val sharedImageLoader by lazy {
        createImageLoader(
            context = context,
            cacheDirectory = context.cacheDir.resolve("image_cache").absolutePath.toPath()
        ).bitmapConfig(android.graphics.Bitmap.Config.RGB_565)
            .build()
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
