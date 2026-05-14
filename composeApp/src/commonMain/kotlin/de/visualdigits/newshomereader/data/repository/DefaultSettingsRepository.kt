package de.visualdigits.newshomereader.data.repository

import androidx.compose.ui.graphics.Color
import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.CryptoBox
import de.visualdigits.common.domain.model.EncryptedString
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.util.toWebColor
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
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
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

    private val log = Logger.withTag("DefaultSettingsRepository")

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
            log.e("Could not load settings", e)
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
            log.e("Could not set settings", e)
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
        displayTheme = get<DisplayThemeEnum>(SK.displayTheme)?.name ?: "LIGHT",
        spotColor = get<Color>(SK.spotColor)?.toWebColor() ?: DisplayThemeEnum.SPOT_COLOR_DEFAULT.toWebColor(),
        language = get<Language>(SK.language)?.name ?: "EN",
        hideRead = get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
        loadArticles = get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false,
        refreshInterval = get<RefreshIntervalEnum>(SK.refreshInterval)?.name ?: "MINUTES_60",
        refreshWifiOnly = get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false,
        lastMaxImageSize = get<Int>(SK.maxImageSize)?.toLong() ?: 1200L,
        keepReadArticles = get<KeepArticlesEnum>(SK.keepReadArticles)?.name ?: "DAYS_30",
        keepUnreadArticles = get<KeepArticlesEnum>(SK.keepUnreadArticles)?.name ?: "DAYS_30",
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
    val displayTheme: String,
    val spotColor: String,
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
