package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import nl.adaptivity.xmlutil.core.impl.multiplatform.InputStream

interface NewsFeedConfigurationRepository {

    suspend fun getNewsFeeds(): Result<NewsFeedConfigurationEntity?, DataError.Local>

    suspend fun setNewsFeeds(newsFeedConfiguration: NewsFeedConfigurationEntity): Result<Unit, DataError.Local>

    /**
     * [ins] must represent a stream of OPML.
     */
    suspend fun setNewsFeeds(ins: InputStream): Result<NewsFeedConfigurationEntity, DataError.Local>
}
