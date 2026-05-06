package de.visualdigits.newshomereader.data.http

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.getKoin

object HttpClientFactory {

    private val log = co.touchlab.kermit.Logger.withTag("HttpClientFactory")

    fun create(
        engine: HttpClientEngine,
               settingsRepositoryProvider: () -> SettingsRepository
    ): HttpClient {
        return HttpClient(engine) {
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
                                log.e("Could not get settings for request", result.throwable)
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
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }
            install(Logging) {
                level = LogLevel.NONE
                logger = object : Logger {
                    override fun log(message: String) {
                        log.d(message)
                    }
                }
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
