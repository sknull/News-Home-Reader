package de.visualdigits.newshomereader.data.repository

import android.content.Context
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.bitmapConfig
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import java.util.concurrent.atomic.AtomicInteger

private val log = kermitLogger("ImageCache")

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
                } catch (e: Exception) {
                    log.e("Could not fetch image: $url", e)
                } finally {
                    onImageDone()
                }
            }
        }
    }
}
