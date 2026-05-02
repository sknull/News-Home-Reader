package de.visualdigits.newshomereader.domain.datasource

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError

interface NewsHomeReaderDataSource {

    suspend fun getString(
        url: String
    ): Result<String, DataError.Remote>
}
