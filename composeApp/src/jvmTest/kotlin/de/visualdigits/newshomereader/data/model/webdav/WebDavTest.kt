package de.visualdigits.newshomereader.data.model.webdav

import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.testModule
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File
import kotlin.time.Clock

@Disabled("Only for local testing")
class WebDavTest : KoinTest {

    private val settingsRepository by inject<SettingsRepository>()
    private val httpClient: HttpClient by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        allowOverride(true)
        modules(platformModule, testModule)
    }

    @Test
    fun testWebDavClient() = runTest {
        val webDavCredentials = Json.decodeFromString<WebDavCredentials>(File("E:\\temp\\.newshomereader\\webDav_credentials.json").readText())
        val newSettings = Settings()
        newSettings.set(SK.displayTheme, DisplayThemeEnum.LIGHT)
        newSettings.set(SK.language, Language.EN)
        newSettings.set(SK.refreshInterval, RefreshIntervalEnum.MINUTES_60)
        newSettings.set(SK.refreshWifiOnly, BooleanEnum.TRUE)
        newSettings.set(SK.maxImageSize, 1200)
        newSettings.set(SK.loadArticles, BooleanEnum.FALSE)
        newSettings.set(SK.hideRead, BooleanEnum.TRUE)
        newSettings.set(SK.keepReadArticles, KeepArticlesEnum.DAYS_3)
        newSettings.set(SK.keepUnreadArticles, KeepArticlesEnum.DAYS_7)
        newSettings.set(SK.webDavUrl, webDavCredentials.webDavUrl)
        newSettings.set(SK.webDavUser, webDavCredentials.webDavUser)
        newSettings.set(SK.webDavPassword, webDavCredentials.webDavPassword)
        settingsRepository.setSettings(newSettings)

//        val response = httpClient.get("${webDavCredentials.webDavUrl}/files/hello.txt")
        val json = """"{ "now": "${Clock.System.now()}", "hello": "world!" }"""
        val response = httpClient.put("${webDavCredentials.webDavUrl}/files/helloWorld.json") {
            setBody(json)
            contentType(ContentType.Application.Json)
        }

        println(response.status)
        println(response.bodyAsText())
    }
}

@Serializable
private data class WebDavCredentials(
    val webDavUrl: String,
    val webDavUser: String,
    val webDavPassword: String
)
