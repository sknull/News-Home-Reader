package de.visualdigits.newshomereader.data.repository

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import okio.Path

@OptIn(ExperimentalCoilApi::class)
fun createImageLoader(
    context: PlatformContext,
    cacheDirectory: Path // okio.Path
): ImageLoader.Builder {
    return ImageLoader.Builder(context)
        .components {
            add(
                KtorNetworkFetcherFactory(
                    httpClient = HttpClient {
                        install(UserAgent) {
                            agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0"
                        }
                        install(HttpTimeout) {
                            requestTimeoutMillis = 10000
                        }
                        install(ContentEncoding) {
                            deflate(1.0F)
                            gzip(0.9F)
                        }
                        defaultRequest {
                            header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                            header("Accept-Encoding", "gzip, deflate, br, zstd")
                            header("Accept-Language", "de,en-US;q=0.9,en;q=0.8")
                        }
                    }
                )
            )
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDirectory)
                .maxSizeBytes(512L * 1024L * 1024L)
                .build()
        }
}
