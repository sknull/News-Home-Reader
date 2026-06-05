package de.visualdigits.newshomereader.data.repository

import android.content.Context
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.bitmapConfig
import io.ktor.client.HttpClient
import kotlinx.coroutines.coroutineScope
import okio.Path.Companion.toPath

private val log = Logger.withTag("ImageCache")

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

    actual suspend fun prefetchImages(urls: List<String>, onImageDone: suspend () -> Unit) {
        coroutineScope {
            urls.forEach { url ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .build()
                try {
                    sharedImageLoader.execute(request)
                } finally {
                    onImageDone()
                }
            }
        }
    }
}
