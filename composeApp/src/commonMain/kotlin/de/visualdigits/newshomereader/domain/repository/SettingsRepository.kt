package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.settings.Settings

interface SettingsRepository {

    suspend fun getSettings(): Result<Settings?, DataError.Local>

    suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local>
}
