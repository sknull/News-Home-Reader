package de.visualdigits.newshomereader.domain.webdav

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError

interface WebDavSyncService {

    suspend fun syncReadStatus(localReadIds: Set<String>): Result<Set<String>, DataError.Remote>
}
