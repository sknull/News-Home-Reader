package de.visualdigits.newshomereader.di

import de.visualdigits.common.domain.model.CryptoBox
import de.visualdigits.common.domain.model.JvmCryptoBox
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.http.HttpClientFactory
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.data.repository.ImageCache
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

actual val homeDirectory: String
    get() = File(System.getProperty("user.home"), ".newshomereader").canonicalPath

actual val platformModule: Module
    get() = module {

        single<CryptoBox> { JvmCryptoBox(get<String>(named("homeDirectory"))) }

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
            HttpClientFactory.create(
                engine = get(),
                settingsRepositoryProvider = { get() } // Holt das SettingsRepository via Koin
            )
        }

        single { FeedScheduler(get()) }
        single { DriverFactory() }
        single { ConnectivityManager() }
        single {
            ImageCache(
                basePath = get<String>(named("homeDirectory")),
                context = coil3.PlatformContext.INSTANCE
            )
        }
    }
