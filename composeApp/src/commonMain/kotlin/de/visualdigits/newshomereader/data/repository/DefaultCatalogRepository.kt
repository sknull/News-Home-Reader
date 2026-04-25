package de.visualdigits.newshomereader.data.repository

import de.visualdigits.compose.resources.Res
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.repository.CatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class DefaultCatalogRepository : CatalogRepository {

    override suspend fun loadCatalog(): Result<NewsFeedCatalog, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val bytes = Res.readBytes("files/catalog.json")
            val json = bytes.decodeToString()
            val newsFeedCatalog = Json.decodeFromString(NewsFeedCatalog.serializer(), json)
            Result.Success(newsFeedCatalog)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }
}
