package de.visualdigits.newshomereader.repository

import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import java.io.File

class MockArticleRepository : ArticleRepository {

    override suspend fun readFromFile(
        itemId: Long,
        file: File
    ): FullArticle {
        return readFromString(itemId, file.readText())
    }

    override suspend fun getFullArticle(itemId: Long): Result<FullArticle?, DataError.Local> {
        return Result.Success(FullArticle(itemId = 4711, html = ""))
    }

    override suspend fun readFullArticle(
        itemId: Long,
        url: String
    ): Result<FullArticle, DataError.Remote> {
        return Result.Success(readFromString(itemId, ""))
    }

    override suspend fun readFromString(
        itemId: Long,
        html: String?
    ): FullArticle {
        return FullArticle(
            itemId = 4711,
            applicationJson = listOf(),
            html = html?:"",
            videoItems = listOf(),
            audioItems = listOf(),
            articleImage = "",
            discussionUrl = "",
            commentCount = 0,
            isFree = true
        )
    }
}
