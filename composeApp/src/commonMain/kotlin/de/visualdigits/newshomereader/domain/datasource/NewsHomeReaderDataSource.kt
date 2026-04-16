package de.visualdigits.newshomereader.domain.datasource

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result

interface NewsHomeReaderDataSource {

    suspend fun getString(
        url: String
    ): Result<String, DataError.Remote>
}
