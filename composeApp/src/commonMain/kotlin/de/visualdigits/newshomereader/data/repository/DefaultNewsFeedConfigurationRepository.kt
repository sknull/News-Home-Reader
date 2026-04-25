package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.NewsFeedGroupEntity
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroup
import de.visualdigits.newshomereader.data.database.mapper.toNewsFeedGroupEntity
import de.visualdigits.newshomereader.data.database.upsertNewsFeed
import de.visualdigits.newshomereader.data.database.upsertNewsFeedGroup
import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.mapper.toOpml
import de.visualdigits.newshomereader.data.model.opml.Opml
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
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

    override suspend fun getNewsFeedGroups(): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        val childrenByParent = dao.getAllNewsFeedGroupEntities()
            .executeAsList()
            .groupBy { it.parentId }
        try {
            val data = childrenByParent[null]
                ?.map { rootEntity ->
                    buildNodeRecursive(rootEntity, childrenByParent)
                } ?: emptyList()
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }

    private fun buildNodeRecursive(
        currentEntity: NewsFeedGroupEntity,
        childrenByParent: Map<Long?, List<NewsFeedGroupEntity>>
    ): NewsFeedGroup {
        val subGroups = childrenByParent[currentEntity.id]?.map { childEntity ->
            buildNodeRecursive(childEntity, childrenByParent)
        } ?: emptyList()

        return NewsFeedGroup(
            id = currentEntity.id,
            name = currentEntity.name,
            newsFeeds = currentEntity.newsFeeds,
            subGroups = subGroups
        )
    }

    override suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while upserting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun upsertNewsFeedGroupSingle(newsFeedGroup: NewsFeedGroup?): Result<NewsFeedGroup?, DataError.Local> = withContext(dispatcher) {
        try {
            checkNotNull(newsFeedGroup) { "NewsFeedGroup is null" }
            dao.upsertNewsFeedGroup(newsFeedGroup.toNewsFeedGroupEntity())
            Result.Success(dao.getNewsFeedGroupEntityByName(newsFeedGroup.name!!, newsFeedGroup.parentGroupName).executeAsOneOrNull()?.toNewsFeedGroup())
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

    override suspend fun deleteNewsFeedItem(newsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = dao.getNewsFeedGroupEntityByName(newsFeedItem.parentGroupName!!, newsFeedItem.rootGroupName).executeAsOneOrNull()
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == newsFeedItem.name }
                dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
                if (newsFeeds.isEmpty()) {
                    deleteNewsFeedGroup(newsFeedGroupEntity.name)
                    if (newsFeedGroupEntity.parentGroupName != null) {
                        val rootEntity = dao.getNewsFeedGroupEntityByName(newsFeedGroupEntity.parentGroupName, null).executeAsOneOrNull()
                        if (rootEntity != null && rootEntity.subGroups.isEmpty()) {
                            deleteNewsFeedGroup(rootEntity.name)
                        }
                    }
                }
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun getNewsFeedGroupByName(parentGroupName: String?, name: String): Result<NewsFeedGroup?, DataError.Local> = withContext(dispatcher) {
        try {
            Result.Success(dao.getNewsFeedGroupEntityByName(name, parentGroupName).executeAsOneOrNull()?.toNewsFeedGroup())
        } catch (e: Exception) {
            log.e("Error while getting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun upsertNewsFeedItem(newsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            checkNotNull(newsFeedItem.parentGroupName) { "Newsfeeditem with name '${newsFeedItem.name}' has no group name" }
            val newsFeedGroupEntity = dao.getNewsFeedGroupEntityByName(newsFeedItem.parentGroupName!!, newsFeedItem.rootGroupName).executeAsOneOrNull()
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == newsFeedItem.name }
                newsFeeds += newsFeedItem
                dao.upsertNewsFeedGroup(newsFeedGroupEntity.copy(newsFeeds = newsFeeds))
            }
            Result.Success(dao.getAllNewsFeedGroupEntities().executeAsList().map { ni -> ni.toNewsFeedGroup() })
        } catch (e: Exception) {
            log.e("Error while deleting group", e)
            Result.Error(DataError.Local.SERIALIZATION, e)
        }
    }

    override suspend fun editNewsFeedItem(oldNewsFeedConfiguration: NewsFeedItem, newNewsFeedConfiguration: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local> = withContext(dispatcher) {
        try {
            val newsFeedGroupEntity = dao.getNewsFeedGroupEntityByName(oldNewsFeedConfiguration.parentGroupName!!, oldNewsFeedConfiguration.rootGroupName).executeAsOneOrNull()
            if (newsFeedGroupEntity != null) {
                val newsFeeds = newsFeedGroupEntity.newsFeeds.toMutableList()
                newsFeeds.removeIf { nf -> nf.name == oldNewsFeedConfiguration.name }
                if (newNewsFeedConfiguration.parentGroupName == oldNewsFeedConfiguration.parentGroupName) {
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
                val groupEntity = dao.getNewsFeedGroupEntityByName(newsFeedGroup.name, newsFeedGroup.parentGroupName).executeAsOneOrNull()
                if (groupEntity != null) {
                    val existingSubGroups = groupEntity.subGroups.map { sg -> sg.name }
                    newsFeedGroup.subGroups
                        .filter { nfg -> !existingSubGroups.contains(nfg.name) }
                        .forEach { sg ->
                            val existingSubGroup = dao.getNewsFeedGroupEntityByName(sg.name, sg.parentGroupName).executeAsOneOrNull()
                            val subGroup = if (existingSubGroup != null) {
                                val existingFeedNames = existingSubGroup.newsFeeds.map { f -> f.name }
                                existingSubGroup.copy(newsFeeds = sg.newsFeeds.filter { nf -> !existingFeedNames.contains(nf.name) })
                            } else {
                                sg.toNewsFeedGroupEntity()
                            }
                            dao.upsertNewsFeedGroup(subGroup)
                        }
                }
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
