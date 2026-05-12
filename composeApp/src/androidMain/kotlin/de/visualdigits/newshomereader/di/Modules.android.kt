package de.visualdigits.newshomereader.di

import de.visualdigits.common.domain.model.AndroidCryptoBox
import de.visualdigits.common.domain.model.CryptoBox
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.newshomereader.data.database.DriverFactory
import de.visualdigits.newshomereader.data.http.HttpClientFactory
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.data.repository.FeedUpdateWorker
import de.visualdigits.newshomereader.data.repository.ImageCache
import de.visualdigits.newshomereader.data.repository.NewsFeedWorker
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

actual val homeDirectory: String
    get() = ""

actual val platformModule: Module
    get() = module {
        single(named("homeDirectoryPath")) {
            File(System.getProperty("user.home"), ".newshomereader")
        }

        single<CryptoBox> { AndroidCryptoBox(get<String>(named("homeDirectory"))) }

        // http engine
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

        single { NewsFeedWorker(get(), get(), get()) }
        worker { FeedUpdateWorker(get(), get()) }
        single { FeedScheduler(get()) }

        single { DriverFactory(androidApplication()) }
        single { ConnectivityManager(get()) }

        single { ImageCache(get(), get()) }
}
