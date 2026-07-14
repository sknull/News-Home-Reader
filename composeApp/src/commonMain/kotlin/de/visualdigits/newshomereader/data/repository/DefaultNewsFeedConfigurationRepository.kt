package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.getAllNewsFeedGroups
import de.visualdigits.newshomereader.data.database.isEqualTo
import de.visualdigits.newshomereader.data.database.toNewsFeedGroup
import de.visualdigits.newshomereader.data.database.toNewsFeedGroupEntity
import de.visualdigits.newshomereader.data.database.upsertNewsFeedGroup
import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.mapper.toOpml
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.domain.mapper.mergeNewsFeedGroups
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import de.visualdigits.newshomereader.domain.repository.NewsFeedConfigurationRepository
import de.visualdigits.newshomereader.domain.util.decodeFromString
import de.visualdigits.newshomereader.domain.util.encodeToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString

class DefaultNewsFeedConfigurationRepository(
    private val dao: NewsHomeReaderDatabaseQueries
) : NewsFeedConfigurationRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getNewsFeedGroups(): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val data = dao.getAllNewsFeedGroups()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }

    override suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(dao.getAllNewsFeedGroups())
        } catch (e: Exception) {
            Logger.e("Error while upserting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun upsertNewsFeedGroupSingle(newsFeedGroup: NewsFeedGroup?): Result<NewsFeedGroup?, DataError.Local> = withContext(dispatcher) {
        try {
            checkNotNull(newsFeedGroup) { "NewsFeedGroup is null" }
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(dao.getNewsFeedGroupEntityByName(newsFeedGroup.name, newsFeedGroup.parentGroupName).executeAsOneOrNull()?.toNewsFeedGroup())
        } catch (e: Exception) {
            Logger.e("Error while upserting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun editNewsFeedGroup(
        newsFeedGroup: NewsFeedGroup?,
        editedNewsFeedGroupName: String
    ): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        if (newsFeedGroup != null) {
            dao.upsertNewsFeedGroup(newsFeedGroup.copy(
                name = editedNewsFeedGroupName
            ).toNewsFeedGroupEntity())
            Result.Success(dao.getAllNewsFeedGroups())
        } else {
            Result.Error(DataError.Local.SERIALIZATION)
        }
    }

    override suspend fun deleteNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.transaction {
                Logger.i("Deleting newsFeedGroup: ${newsFeedGroup.name}")
                dao.deleteNewsFeedGroupEntity(newsFeedGroup.id)
                dao.deleteNewsFeedByFeedName(newsFeedGroup.name)
                dao.deleteNewsItemsByFeedName(newsFeedGroup.name.trim().lowercase())
                newsFeedGroup.subGroups.forEach { subGroup ->
                    dao.deleteNewsFeedGroupEntity(subGroup.id)
                    Logger.i("Deleting newsFeedGroup: ${subGroup.name}")
                    dao.deleteNewsFeedByFeedName(subGroup.name)
                    dao.deleteNewsItemsByFeedName(subGroup.name.trim().lowercase())
                }
            }
            Result.Success(dao.getAllNewsFeedGroups())
        } catch (e: Exception) {
            Logger.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun deleteNewsFeedItem(newsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            Logger.i("Deleting newsFeedItem: ${newsFeedItem.name}")
            val newsFeedGroupEntity = getNewsFeedGroupEntity(newsFeedItem)
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == newsFeedItem.name }
                dao.transaction {
                    dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
                    newsFeedItem.name?.also { name ->
                        Logger.i("Deleting news items for newsfeed: ${newsFeedItem.name}")
                        dao.deleteNewsItemsByFeedName(name.trim().lowercase())
                    }
                }
            }
            Result.Success(dao.getAllNewsFeedGroups())
        } catch (e: Exception) {
            Logger.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedGroupByName(parentGroupName: String?, name: String): Result<NewsFeedGroup?, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getNewsFeedGroupEntityByName(name, parentGroupName).executeAsOneOrNull()?.toNewsFeedGroup())
        } catch (e: Exception) {
            Logger.e("Error while getting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun upsertNewsFeedItem(newsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = dao.getNewsFeedGroupEntityByName(newsFeedItem.subGroupName ?: newsFeedItem.mainGroupName, if (newsFeedItem.subGroupName != null) newsFeedItem.mainGroupName else null).executeAsOneOrNull()
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == newsFeedItem.name }
                newsFeeds += newsFeedItem
                dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
            }
            Result.Success(dao.getAllNewsFeedGroups())
        } catch (e: Exception) {
            Logger.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun editNewsFeedItem(oldNewsFeedItem: NewsFeedItem, newNewsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val oldNewsFeedGroupEntity = getNewsFeedGroupEntity(oldNewsFeedItem)
            val newNewsFeedGroupEntity = getNewsFeedGroupEntity(newNewsFeedItem)
            if (newNewsFeedGroupEntity != null) {
                if (oldNewsFeedGroupEntity?.isEqualTo(newNewsFeedGroupEntity) == true) {
                    dao.upsertNewsFeedGroup(newNewsFeedGroupEntity
                        .copy(newsFeeds = newNewsFeedGroupEntity.newsFeeds - oldNewsFeedItem + newNewsFeedItem))
                } else {
                    val oldCopy = oldNewsFeedGroupEntity?.copy(newsFeeds = oldNewsFeedGroupEntity.newsFeeds - oldNewsFeedItem)
                    val newCopy = newNewsFeedGroupEntity.copy(newsFeeds = newNewsFeedGroupEntity.newsFeeds + newNewsFeedItem)
                    dao.transaction {
                        oldCopy?.also { g -> dao.upsertNewsFeedGroup(g) }
                        dao.upsertNewsFeedGroup(newCopy)
                    }
                }
            }
            val data = dao.getAllNewsFeedGroups()
            Result.Success(data)
        } catch (e: Exception) {
            Logger.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    private fun getNewsFeedGroupEntity(newsFeedItem: NewsFeedItem): NewsFeedGroupEntity? =
        if (newsFeedItem.subGroupName != null) {
            dao.getNewsFeedGroupEntityByName(
                parentGroupName = newsFeedItem.mainGroupName,
                name = newsFeedItem.subGroupName?:error("No sub group given")
            ).executeAsOneOrNull()
        } else {
            dao.getNewsFeedGroupEntityByName(
                parentGroupName = null,
                name = newsFeedItem.mainGroupName
            ).executeAsOneOrNull()
        }

    override suspend fun setNewsFeedGroups(source: Source): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val xml = source.use { source ->
                source.readString()
            }
            val newsFeedGroups = decodeFromString<Opml>(xml, false)
                .toNewsFeedConfiguration()
            dao.transaction {
                val existingNewsFeedGroups = dao.getAllNewsFeedGroups()
                val mergedNewsFeedGroups = existingNewsFeedGroups.mergeNewsFeedGroups(newsFeedGroups)
                mergedNewsFeedGroups.forEach { mfg -> persistNewsFeedGroup(mfg) } }
            Result.Success(dao.getAllNewsFeedGroups())
        } catch (e: Exception) {
            Logger.e("Error while deleting groups", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    private fun persistNewsFeedGroup(newsfeedGroup: NewsFeedGroup) {
        val insertedNewsFeedGroup = dao.upsertNewsFeedGroup(newsfeedGroup.toNewsFeedGroupEntity())
        newsfeedGroup.subGroups.forEach { subGroup ->
            persistNewsFeedGroup(subGroup.copy(parentId = insertedNewsFeedGroup.id))
        }
    }

    override suspend fun saveNewsFeedGroups(sink: Sink): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val opml = dao.getAllNewsFeedGroups().toOpml()
            val xml = encodeToString(Opml.serializer(), opml)
            sink.use { writer ->
                writer.writeString(xml)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Error while saving groups", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }
}
