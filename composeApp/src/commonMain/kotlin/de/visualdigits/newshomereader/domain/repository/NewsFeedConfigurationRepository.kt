package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream

interface NewsFeedConfigurationRepository {

    suspend fun getNewsFeedGroups(): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun getNewsFeedGroupByName(name: String): Result<NewsFeedGroup?, DataError.Local>

    suspend fun setNewsFeedGroups(newsFeedGroups: List<NewsFeedGroup>): Result<List<NewsFeedGroup>, DataError.Local>

    /**
     * [ins] must represent a stream of OPML.
     */
    suspend fun setNewsFeedGroups(ins: InputStream): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun deleteNewsFeedGroup(newsFeedGroupName: String): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun upsertNewsFeedConfiguration(newsFeedConfiguration: NewsFeedConfiguration): Result<List<NewsFeedGroup>, DataError.Local>
}
