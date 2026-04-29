package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.settings.Settings
import java.io.InputStream
import java.io.OutputStream

interface SettingsRepository {

    suspend fun getSettings(): Result<Settings?, DataError.Local>

    suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local>

    suspend fun importSettings(ins: InputStream): Result<Settings, DataError.Local>

    suspend fun exportSettings(settings: Settings, outs: OutputStream): Result<Unit, DataError.Local>
}
