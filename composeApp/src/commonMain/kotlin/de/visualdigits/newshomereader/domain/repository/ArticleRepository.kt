package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import java.io.File

interface ArticleRepository {

    suspend fun readFromFile(
        newsItem: NewsItem,
        file: File
    ): FullArticle

    suspend fun getFullArticle(
        itemId: Long,
    ): Result<FullArticle?, DataError.Local>

    suspend fun readFullArticle(
        newsItem: NewsItem,
    ): Result<FullArticle, DataError.Remote>

    suspend fun readFromString(
        newsItem: NewsItem,
        rawHtml: String?
    ): FullArticle
}
