package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.settings.Settings
import kotlinx.io.Sink
import kotlinx.io.Source

interface SettingsRepository {

    var webDavUrl: String?

    suspend fun getSettings(): Result<Settings?, DataError.Local>

    suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local>

    suspend fun importSettings(source: Source): Result<Settings, DataError.Local>

    suspend fun exportSettings(settings: Settings, sink: Sink): Result<Unit, DataError.Local>
}
