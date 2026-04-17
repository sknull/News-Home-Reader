package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream

interface NewsFeedConfigurationRepository {

    suspend fun upsertNewsFeedGroup(newsFeedGroup: NewsFeedGroup): Result<Unit, DataError.Local>

    suspend fun getNewsFeeds(): Result<List<NewsFeedGroup>, DataError.Local>

    suspend fun setNewsFeeds(newsFeedGroups: List<NewsFeedGroup>): Result<Unit, DataError.Local>

    /**
     * [ins] must represent a stream of OPML.
     */
    suspend fun setNewsFeeds(ins: InputStream): Result<List<NewsFeedGroup>, DataError.Local>
}
