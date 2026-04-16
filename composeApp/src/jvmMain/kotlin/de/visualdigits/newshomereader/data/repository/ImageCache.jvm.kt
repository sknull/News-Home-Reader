package de.visualdigits.newshomereader.data.repository

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okio.Path.Companion.toPath

actual class ImageCache(
    private val context: PlatformContext
) {
    private val prefetchScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val sharedImageLoader by lazy {
        createImageLoader(
            context = context,
            cacheDirectory = System.getProperty("user.home").toPath() / ".newshomereader" / "image_cache"
        )
    }

    actual fun getImageLoader(): ImageLoader = sharedImageLoader

    actual fun prefetchImages(urls: List<String>) {
        prefetchScope.launch {
            val semaphore = Semaphore(3)
            urls.forEach { url ->
                launch {
                    semaphore.withPermit {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build()
                        sharedImageLoader.execute(request)
                    }
                }
            }
        }
    }
}
