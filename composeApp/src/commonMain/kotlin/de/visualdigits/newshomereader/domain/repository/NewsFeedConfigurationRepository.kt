package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream
import nl.adaptivity.xmlutil.core.impl.multiplatform.OutputStream

interface NewsFeedConfigurationRepository {

    suspend fun getNewsFeedGroups(): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun getNewsFeedGroupByName(parentGroupName: String?, name: String): Result<NewsFeedGroup?, DataError.Local>

    suspend fun setNewsFeedGroups(newsFeedGroups: List<NewsFeedGroup>): Result<List<NewsFeedGroup>, DataError.Local>

    /**
     * [ins] must represent a stream of OPML.
     */
    suspend fun setNewsFeedGroups(ins: InputStream): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun saveNewsFeedGroups(outs: OutputStream): Result<Unit, DataError.Local>

    suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun upsertNewsFeedGroupSingle(newsFeedGroup: NewsFeedGroup?): Result<NewsFeedGroup?, DataError.Local>

    suspend fun editNewsFeedGroup(newsFeedGroup: NewsFeedGroup?, editedNewsFeedGroupName: String): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun deleteNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun deleteNewsFeedItem(newsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun upsertNewsFeedItem(newsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun editNewsFeedItem(oldNewsFeedItem: NewsFeedItem, newNewsFeedItem: NewsFeedItem): Result<List<NewsFeedGroup>, DataError.Local>
}
