package de.visualdigits.newshomereader.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.repository.DefaultArticleRepository
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.io.File

class MockArticleRepository(
    private val httpClient: HttpClient,
    private val dao: NewsHomeReaderDatabaseQueries
) : DefaultArticleRepository(
    httpClient = httpClient,
    dao = dao
) {

    override suspend fun readFromFile(
        newsItem: NewsItem,
        file: File
    ): FullArticle {
        return readFromString(newsItem, file.readText())
    }

    override suspend fun getFullArticle(itemId: Long): Result<FullArticle?, DataError.Local> {
        return Result.Success(FullArticle(id = 1234, itemId = 4711, html = ""))
    }

    override suspend fun readFullArticle(
        newsItem: NewsItem
    ): Result<Pair<FullArticle, Boolean>, DataError.Remote> {
        val response = httpClient.get(urlString = newsItem.link)
        val rawHtml = response.bodyAsText()
        val data = readFromString(newsItem, rawHtml, newsItem.link)
        return Result.Success(Pair(data, false))
    }
}
