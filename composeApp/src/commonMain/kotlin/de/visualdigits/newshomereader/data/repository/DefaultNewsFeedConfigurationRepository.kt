package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroup
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroupEntity
import de.visualdigits.newshomereader.data.database.upsertNewsFeedGroup
import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.mapper.toOpml
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import de.visualdigits.newshomereader.domain.util.encodeToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream
import nl.adaptivity.xmlutil.core.impl.multiplatform.OutputStream

class DefaultNewsFeedConfigurationRepository(
    private val dao: NewsHomeReaderDatabaseQueries
) : NewsFeedConfigurationRepository {

    private val log = kermitLogger(DefaultNewsFeedConfigurationRepository::class)

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while upserting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun deleteNewsFeedGroup(newsFeedGroupName: String): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.deleteNewsFeedGroupEntityByName(newsFeedGroupName)
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun deleteNewsFeedConfiguration(newsFeedConfiguration: NewsFeedConfigurationEntity): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = newsFeedConfiguration.groupName?.let { gn -> dao.getNewsFeedGroupEntityByName(gn).executeAsOneOrNull() }
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == newsFeedConfiguration.name }
                dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedGroups(): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getAllNewsFeedGroupEntities()
                .executeAsList()
                .map { nf -> nf.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting groups", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedGroupByName(name: String): Result<NewsFeedGroup?, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getNewsFeedGroupEntityByName(name).executeAsOneOrNull()?.toNewsFeedGroup())
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun upsertNewsFeedConfiguration(newsFeedConfiguration: NewsFeedConfigurationEntity): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = newsFeedConfiguration.groupName?.let { gn -> dao.getNewsFeedGroupEntityByName(gn).executeAsOneOrNull() }
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == newsFeedConfiguration.name }
                newsFeeds += newsFeedConfiguration
                dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun editNewsFeedConfiguration(oldNewsFeedConfiguration: NewsFeedConfigurationEntity, newNewsFeedConfiguration: NewsFeedConfigurationEntity): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = oldNewsFeedConfiguration.groupName?.let { gn -> dao.getNewsFeedGroupEntityByName(gn).executeAsOneOrNull() }
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == oldNewsFeedConfiguration.name }
                if (newNewsFeedConfiguration.groupName == oldNewsFeedConfiguration.groupName) {
                    newsFeeds += newNewsFeedConfiguration
                    dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
                }
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
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
            log.e("Error while deleting groups", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun setNewsFeedGroups(ins: InputStream): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroups = decodeFromString<Opml>(String(ins.readBytes()), false)
                .toNewsFeedConfiguration()
            setNewsFeedGroups(newsFeedGroups)
            Result.Success(newsFeedGroups)
        } catch (e: Exception) {
            log.e("Error while deleting groups", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun saveNewsFeedGroups(outs: OutputStream): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val opml = dao.getAllNewsFeedGroupEntities()
                .executeAsList()
                .map { nfg -> nfg.toNewsFeedGroup()}
                .toOpml()
            val xml = encodeToString(Opml.serializer(), opml)
            outs.writer().use { writer ->
                writer.write(xml)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            log.e("Error while saving groups", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }
}
