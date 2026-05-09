package de.visualdigits.newshomereader.domain.model.settings

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.data.database.mapper.toSettings
import de.visualdigits.newshomereader.data.database.mapper.toSettingsEntity
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.di.testHomeDirectory
import de.visualdigits.newshomereader.di.testModule
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.io.File

@Disabled("Only for local testing")
class SettingsTest : KoinTest {

    private val settingsRepository by inject<SettingsRepository>()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        allowOverride(true)
        modules(sharedModule, platformModule, testModule)
    }

    @Test
    fun testSettingsCredentials() = runTest {
        File(testHomeDirectory, "settings.db").delete()

        val setResult = settingsRepository.setSettings(Settings())
        assert(setResult is Result.Success)

        val initialResult = settingsRepository.getSettings()
        assert(initialResult is Result.Success)

        val data = (initialResult as Result.Success).data
        assertNotNull(data)

        val testSettingsEntity = data.toSettingsEntity().copy(
            webDavUrl = "http://192.168.1.100:5005",
            webDavUser = "testuser",
            webDavPassword = "password",
            language = "DE"
        )

        val testSettings = testSettingsEntity.toSettings()
        val saveResult = settingsRepository.setSettings(testSettings)
        assert(saveResult is Result.Success)

        val loadResult = settingsRepository.getSettings()
        assert(loadResult is Result.Success)

        val loadedData = (loadResult as Result.Success).data
        assertNotNull(loadedData)
        val loadedEntity = loadedData.toSettingsEntity()

        assertEquals(testSettingsEntity.webDavUrl, loadedEntity.webDavUrl)
        assertEquals(testSettingsEntity.webDavUser, loadedEntity.webDavUser)
        assertEquals(testSettingsEntity.webDavPassword, loadedEntity.webDavPassword)
        assertEquals(testSettingsEntity.language, loadedEntity.language)
    }
}
