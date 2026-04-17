package de.visualdigits.newshomereader.di

import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.data.repository.ImageCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single {
            HttpClient(get<HttpClientEngine>()) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 15000
                    connectTimeoutMillis = 10000
                    socketTimeoutMillis = 15000
                }
                defaultRequest {
                    header(HttpHeaders.AcceptCharset, "utf-8")
                    header(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0")
                }
            }
        }
        single { FeedScheduler(get()) }
        single { DriverFactory() }
        single { ConnectivityManager() }
        single { ImageCache(coil3.PlatformContext.INSTANCE, get()) }
}
