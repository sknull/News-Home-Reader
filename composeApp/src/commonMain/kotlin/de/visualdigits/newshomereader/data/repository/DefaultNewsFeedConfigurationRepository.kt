package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.insertNewsFeedGroup
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroup
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroupEntity
import de.visualdigits.newshomereader.data.database.updateNewsFeedGroup
import de.visualdigits.newshomereader.data.database.upsertNewsFeedGroup
import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
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

    override suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeeds(): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getAllNewsFeedGroupEntities()
                .executeAsList()
                .map { nf -> nf.toNewsFeedGroup() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    suspend fun addNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            dao.updateNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    suspend fun addNewsFeedConfiguration(newsFeedConfiguration: NewsFeedConfiguration): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val entity = dao.getNewsFeedGroupEntityByName(newsFeedConfiguration.groupName).executeAsOneOrNull()
            if (entity != null) {
                val map = entity.newsFeeds.associateBy { nf -> nf.name }
                if (!map.containsKey(newsFeedConfiguration.name)) {
                    dao.upsertNewsFeedGroup(entity.copy(newsFeeds = entity.newsFeeds + newsFeedConfiguration))
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun setNewsFeeds(newsFeedGroups: List<NewsFeedGroup>): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            newsFeedGroups.forEach { newsFeedGroup ->
                dao.insertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun setNewsFeeds(ins: InputStream): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroups = decodeFromString<Opml>(String(ins.readBytes()))
                .toNewsFeedConfiguration()
            setNewsFeeds(newsFeedGroups)
            Result.Success(newsFeedGroups)
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }
}
