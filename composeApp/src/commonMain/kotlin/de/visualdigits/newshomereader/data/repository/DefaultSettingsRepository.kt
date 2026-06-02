package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.CryptoBox
import de.visualdigits.common.domain.model.EncryptedString
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.LogMessage.Companion.log
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.presentation.components.StudioClockColors
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.toSettings
import de.visualdigits.newshomereader.data.database.toSettingsEntity
import de.visualdigits.newshomereader.data.database.upsertSettings
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.presentation.style.BACKGROUND_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.SPOT_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.TEXT_COLOR_DEFAULT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import java.io.InputStream
import java.io.OutputStream

class DefaultSettingsRepository(
    private val dao: NewsHomeReaderDatabaseQueries,
    private val cryptoBox: CryptoBox
): SettingsRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override var webDavUrl: String? = null

    override suspend fun getSettings(): Result<Settings?, DataError .Local> = withContext(dispatcher) {
        try {
            dao.getSettingsById(0)
                .executeAsOneOrNull()
                ?.let { settingsEntity ->
                    settingsEntity
                        .toSettings()
                        .let { s ->
                            webDavUrl = s.get<String>(SK.webDavUrl) // remember url for update worker
                            Result.Success(s)
                        }
                } ?: Result.Success(null)
        } catch (e: Exception) {
            log(Severity.Error, "Could not load settings", e, withTag = "NHR")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            webDavUrl = settings.get<String>(SK.webDavUrl)
            val settingsEntity = settings.toSettingsEntity()
            dao.upsertSettings(settingsEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            log(Severity.Error, "Could not set settings", e, withTag = "NHR")
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun importSettings(ins: InputStream): Result<Settings, DataError.Local> = withContext(dispatcher) {
        try {
            val jsonMapper = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
            val json = ins.use { ins ->
                String(ins.readBytes())
            }

            val settings = Settings(
                valueMap(
                    fieldDescriptors = Settings.DESCRIPTORS,
                    values = jsonMapper
                        .decodeFromString<Map<String, JsonElement>>(json)
                        .mapNotNull { (key, value) ->
                            val sk = SK.fromString(key)
                            if (sk != null) {
                                val rawValue = value.jsonPrimitive.content
                                val finalValue = if (sk == SK.webDavPassword) {
                                    cryptoBox.decrypt(rawValue)
                                } else {
                                    rawValue
                                }
                                Pair(sk, finalValue)
                            } else {
                                null
                            }
                        }
                        .toMap()
                ))
            setSettings(settings)
            Result.Success(settings)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }

    override suspend fun exportSettings(settings: Settings, outs: OutputStream): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val jsonMapper = Json {
                prettyPrint = true
            }
            val value = settings.toSettingsRepositoryEntity(cryptoBox)
            val json = jsonMapper.encodeToString(value)
            outs.writer().use { writer ->
                writer.write(json)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }
}

private fun Settings.toSettingsRepositoryEntity(cryptoBox: CryptoBox): SettingsRepositoryEntity {
    val settingsEntity = SettingsRepositoryEntity(
        id = 0,
        backgroundColor = get<HsvColor>(SK.backgroundColor)?.hex() ?: BACKGROUND_COLOR_DEFAULT.hex(),
        buttonColor = get<HsvColor>(SK.buttonColor)?.hex() ?: BUTTON_COLOR_DEFAULT.hex(),
        textColor = get<HsvColor>(SK.textColor)?.hex() ?: TEXT_COLOR_DEFAULT.hex(),
        spotColor = get<HsvColor>(SK.spotColor)?.hex() ?: SPOT_COLOR_DEFAULT.hex(),
        clockColor = get<HsvColor>(SK.clockColor)?.hex() ?: StudioClockColors.STUDIO_CLOCK_COLOR_DEFAULT.hex(),
        language = get<Language>(SK.language)?.name ?: "EN",
        refreshInterval = get<RefreshIntervalEnum>(SK.refreshInterval)?.name ?: "MINUTES_60",
        refreshWifiOnly = get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false,
        lastMaxImageSize = get<Int>(SK.maxImageSize)?.toLong() ?: 1200L,
        keepReadArticles = get<KeepArticlesEnum>(SK.keepReadArticles)?.name ?: "DAYS_30",
        keepUnreadArticles = get<KeepArticlesEnum>(SK.keepUnreadArticles)?.name ?: "DAYS_30",
        loadArticles = get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false,
        hideRead = get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
        webDavUrl = get<String>(SK.webDavUrl) ?: "",
        webDavDirectory = get<String>(SK.webDavDirectory) ?: "",
        webDavUser = get<String>(SK.webDavUser) ?: "",
        webDavPassword = get<String>(SK.webDavPassword)?.let { pw -> cryptoBox.encrypt(pw) } ?: "",
    )
    return settingsEntity
}

@Serializable
private data class SettingsRepositoryEntity(
    val id: Long,
    val backgroundColor: String,
    val buttonColor: String,
    val textColor: String,
    val spotColor: String,
    val clockColor: String,
    val language: String,
    val refreshInterval: String,
    val refreshWifiOnly: Boolean,
    val lastMaxImageSize: Long,
    val keepReadArticles: String,
    val keepUnreadArticles: String,
    val loadArticles: Boolean,
    val hideRead: Boolean,
    val webDavUrl: String,
    val webDavDirectory: String,
    val webDavUser: String,
    val webDavPassword: EncryptedString,
)
