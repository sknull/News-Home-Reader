package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okio.Path.Companion.toPath

private val log = Logger.withTag("ImageCache")

actual class ImageCache(
    private val context: PlatformContext,
    private val httpClient: HttpClient
) {
    private val prefetchScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val sharedImageLoader by lazy {
        createImageLoader(
            context = context,
            cacheDirectory = System.getProperty("user.home").toPath() / ".newshomereader" / "image_cache"
        ).build()
    }

    actual fun getImageLoader(): ImageLoader = sharedImageLoader

    actual suspend fun prefetchImages(urls: List<String>, onImageDone: suspend () -> Unit) {
        coroutineScope {
            val semaphore = Semaphore(3)
            urls.forEach { url ->
                launch {
                    semaphore.withPermit {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build()
                        try {
                            sharedImageLoader.execute(request)
                        } catch (e: Exception) {
                            log.e("Could not fetch image: $url", e)
                        } finally {
                            onImageDone()
                        }
                    }
                }
            }
        }
    }
}
