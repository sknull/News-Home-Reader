package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.upsertSettings
import de.visualdigits.newshomereader.data.mapper.toSettings
import de.visualdigits.newshomereader.data.mapper.toSettingsEntity
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DefaultSettingsRepository(
    private val dao: NewsHomeReaderDatabaseQueries
): SettingsRepository {

    private val log = kermitLogger(this::class)

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getSettings(): Result<Settings?, DataError .Local> = withContext(dispatcher) {
        try {
            dao.getSettingsById(0)
                .executeAsOneOrNull()
                ?.let { settingsEntites ->
                    settingsEntites
                        .toSettings()
                        .let { s -> Result.Success(s) }
                } ?: Result.Success(null)
        } catch (e: Exception) {
            log.e("Could not load settings", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val settingsEntity = settings.toSettingsEntity()
            dao.upsertSettings(settingsEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            log.e("Could not set settings", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
