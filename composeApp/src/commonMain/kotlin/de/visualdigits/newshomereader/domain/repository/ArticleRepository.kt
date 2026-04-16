package de.visualdigits.newshomereader.domain.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import java.io.File

interface ArticleRepository {

    suspend fun readFromFile(
        itemId: Long,
        file: File
    ): FullArticle

    suspend fun getFullArticle(
        itemId: Long,
    ): Result<FullArticle?, DataError.Local>

    suspend fun readFullArticle(
        itemId: Long,
        url: String
    ): Result<FullArticle, DataError.Remote>

    suspend fun readFromString(
        itemId: Long,
        html: String?
    ): FullArticle
}
