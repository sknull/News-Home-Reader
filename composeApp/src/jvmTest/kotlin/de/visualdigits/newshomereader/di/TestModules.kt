package de.visualdigits.newshomereader.di

import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import de.visualdigits.newshomereader.domain.repository.FeedRepository
import de.visualdigits.newshomereader.repository.MockArticleRepository
import de.visualdigits.newshomereader.repository.MockFeedRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.koin.dsl.module

val testModule = module {
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
    single {
        HttpClient(get<HttpClientEngine>()) {
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 15000
            }
            defaultRequest {
                header(HttpHeaders.AcceptCharset, "utf-8")
                header(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:149.0) Gecko/20100101 Firefox/149.0"
                )
            }
        }
    }
    single<FeedRepository> { MockFeedRepository(get()) }
    single<ArticleRepository> { MockArticleRepository(get()) }
}
