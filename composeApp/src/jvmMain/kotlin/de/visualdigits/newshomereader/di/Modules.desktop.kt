package de.visualdigits.newshomereader.di

import co.touchlab.kermit.Logger
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.data.repository.ImageCache
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.koin.core.module.Module
import org.koin.dsl.module

val log = Logger.withTag("HttpClient")

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> {
            OkHttp.create {
                config {
                    followRedirects(false)
                    followSslRedirects(false)
                    // limits parallel connections to avoid jam
                    dispatcher(okhttp3.Dispatcher().apply {
                        maxRequestsPerHost = 4
                    })
                }
            }
        }
        single {
            HttpClient(get<HttpClientEngine>()) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 15000
                    connectTimeoutMillis = 10000
                    socketTimeoutMillis = 15000
                }
                install(HttpRedirect) {
                    checkHttpMethod = false
                    allowHttpsDowngrade = true
                }
//                install(Logging) {
//                    level = LogLevel.NONE
//                    logger = object : Logger {
//                        override fun log(message: String) {
//                            log.d(message)
//                        }
//                    }
//                }
                followRedirects = true
                defaultRequest {
                    header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    header(HttpHeaders.AcceptCharset, "utf-8")
                    header(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/150.0")
                }
            }
        }
        single { FeedScheduler(get()) }
        single { DriverFactory() }
        single { ConnectivityManager() }
        single { ImageCache(coil3.PlatformContext.INSTANCE, get()) }
    }
