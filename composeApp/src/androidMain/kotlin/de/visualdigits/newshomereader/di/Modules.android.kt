package de.visualdigits.newshomereader.di

import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.data.repository.FeedUpdateWorker
import de.visualdigits.newshomereader.data.repository.ImageCache
import de.visualdigits.newshomereader.data.repository.NewsFeedWorker
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
        // http engine
        single<HttpClientEngine> {
            OkHttp.create {
                config {
                    // limits parallel connections to avoid jam
                    dispatcher(okhttp3.Dispatcher().apply {
                        maxRequestsPerHost = 4
                    })
                }
            }
        }

        // global http client for all other calls
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

        single { NewsFeedWorker(get(), get(), get()) }
        worker { FeedUpdateWorker(get(), get()) }
        single { FeedScheduler(get()) }

        single { DriverFactory(androidApplication()) }
        single { ConnectivityManager(get()) }

        single { ImageCache(get(), get()) }
}
