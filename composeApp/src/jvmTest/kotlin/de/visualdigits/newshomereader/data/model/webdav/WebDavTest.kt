package de.visualdigits.newshomereader.data.model.webdav

import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.StudioClockColors
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
        val newSettings = Settings(mapOf(
            SK.displayTheme to DisplayThemeEnum.LIGHT,
            SK.clockColor to StudioClockColors.STUDIO_CLOCK_COLOR_DEFAULT,
            SK.spotColor to DisplayThemeEnum.SPOT_COLOR_DEFAULT,
            SK.language to Language.EN,
            SK.refreshInterval to RefreshIntervalEnum.MINUTES_60,
            SK.refreshWifiOnly to BooleanEnum.TRUE,
            SK.maxImageSize to 1200,
            SK.loadArticles to BooleanEnum.FALSE,
            SK.hideRead to BooleanEnum.TRUE,
            SK.keepReadArticles to KeepArticlesEnum.DAYS_3,
            SK.keepUnreadArticles to KeepArticlesEnum.DAYS_7,
            SK.webDavUrl to webDavCredentials.webDavUrl,
            SK.webDavUser to webDavCredentials.webDavUser,
            SK.webDavPassword to webDavCredentials.webDavPassword
        ))
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
