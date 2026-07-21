package de.visualdigits.newshomereader.data.http

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin

object HttpClientFactory {

    fun create(
        engine: HttpClientEngine,
               settingsRepositoryProvider: () -> SettingsRepository
    ): HttpClient {
        return HttpClient(engine) {
            install(UserAgent) {
                agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0"
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                socketTimeoutMillis = 10000
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Auth) {
                basic {
                    credentials {
                        val result = settingsRepositoryProvider().getSettings()
                            if (result is Result.Success) {
                                val settings = result.data
                                val user = settings?.get<String>(SK.webDavUser)
                                val pass = settings?.get<String>(SK.webDavPassword)
                                if (!user.isNullOrBlank() && !pass.isNullOrBlank()) {
                                    BasicAuthCredentials(user, pass)
                                } else {
                                    null
                                }
                            } else if (result is Result.Error) {
                                Logger.e("Could not get settings for request", result.throwable)
                                null
                            } else {
                                null
                            }
                    }

                    cacheTokens = false

                    sendWithoutRequest { request ->
                        val settingsrepository = getKoin().get<SettingsRepository>()
                        val webDavUrl = Url(settingsrepository.webDavUrl?:"")
                        request.url.host == webDavUrl.host && request.url.port == webDavUrl.port
                    }
                }
            }
            install(Logging) {
                level = LogLevel.NONE
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.i(message)
                    }
                }
            }
            defaultRequest {
                header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                header("Accept-Encoding", "gzip, deflate, zstd")
                header("Accept-Language", "de,en-US;q=0.9,en;q=0.8")
            }
        }
    }
}
