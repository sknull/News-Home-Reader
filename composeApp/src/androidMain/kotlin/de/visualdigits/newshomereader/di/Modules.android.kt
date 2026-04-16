package de.visualdigits.newshomereader.di

import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.data.repository.FeedUpdateWorker
import de.visualdigits.newshomereader.data.repository.ImageCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
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
                }
            }
        }
        single { FeedScheduler(get()) }
        worker { FeedUpdateWorker(get(), get(), get()) }
        single { DriverFactory(androidApplication()) }
        single { ConnectivityManager(get()) }
        single { ImageCache(get()) }
}
