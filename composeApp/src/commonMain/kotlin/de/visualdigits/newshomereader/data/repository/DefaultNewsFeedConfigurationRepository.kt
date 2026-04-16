package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream

class DefaultNewsFeedConfigurationRepository(
    private val dao: NewsHomeReaderDatabaseQueries
) : NewsFeedConfigurationRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getNewsFeeds(): Result<NewsFeedConfigurationEntity?, DataError.Local> = withContext(dispatcher) {
        try {
            dao.getAllNewsFeedConfigurations()
                .executeAsList()
                .firstOrNull()
                ?.let { blob ->
                    Result.Success(blob.jsonContent)
                }
                ?: Result.Success(null)
        } catch (_: Exception) {
            Result.Error(DataError.Local.SERIALIZATION)
        }
    }

    override suspend fun setNewsFeeds(newsFeedConfiguration: NewsFeedConfigurationEntity): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            dao.insertNewsFeedConfiguration(newsFeedConfiguration)
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Error(DataError.Local.SERIALIZATION)
        }
    }

    override suspend fun setNewsFeeds(ins: InputStream): Result<NewsFeedConfigurationEntity, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedConfiguration = decodeFromString<Opml>(String(ins.readBytes())).toNewsFeedConfiguration()
            dao.insertNewsFeedConfiguration(newsFeedConfiguration)
            Result.Success(newsFeedConfiguration)
        } catch (_: Exception) {
            Result.Error(DataError.Local.SERIALIZATION)
        }
    }
}
