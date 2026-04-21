package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroup
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroupEntity
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

    override suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun deleteNewsFeedGroup(newsFeedGroupName: String): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.deleteNewsFeedGroupEntityByName(newsFeedGroupName)
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedGroups(): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getAllNewsFeedGroupEntities()
                .executeAsList()
                .map { nf -> nf.toNewsFeedGroup() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedGroupByName(name: String): Result<NewsFeedGroup?, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getNewsFeedGroupEntityByName(name).executeAsOneOrNull()?.toNewsFeedGroup())
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun upsertNewsFeedConfiguration(newsFeedConfiguration: NewsFeedConfiguration): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = dao.getNewsFeedGroupEntityByName(newsFeedConfiguration.groupName).executeAsOneOrNull()
            if (newsFeedGroupEntity != null) {
                dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeedGroupEntity.newsFeeds - newsFeedConfiguration + newsFeedConfiguration))
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun setNewsFeedGroups(newsFeedGroups: List<NewsFeedGroup>): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            newsFeedGroups.forEach { newsFeedGroup ->
                dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun setNewsFeedGroups(ins: InputStream): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroups = decodeFromString<Opml>(String(ins.readBytes()))
                .toNewsFeedConfiguration()
            setNewsFeedGroups(newsFeedGroups)
            Result.Success(newsFeedGroups)
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }
}
