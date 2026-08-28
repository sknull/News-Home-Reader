package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem

interface ArticleRepository {

    suspend fun getFullArticle(
        itemId: Long,
    ): Result<FullArticle?, DataError.Local>

    suspend fun readFullArticle(
        newsItem: NewsItem,
    ): Result<Pair<FullArticle?, Boolean>, DataError.Remote>

    suspend fun readFromString(
        newsItem: NewsItem? = null,
        rawHtml: String,
        url: String? = null
    ): FullArticle
}
