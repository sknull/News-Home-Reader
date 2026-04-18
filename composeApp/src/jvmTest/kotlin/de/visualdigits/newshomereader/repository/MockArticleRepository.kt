package de.visualdigits.newshomereader.repository

import de.visualdigits.newshomereader.data.mapper.toAppJson
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonDto
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import io.github.cdimascio.essence.Essence
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import kotlin.math.roundToLong

class MockArticleRepository(
    private val httpClient: HttpClient,
) : ArticleRepository {

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
        val response = httpClient?.get(urlString = url)
        val rawHtml = response?.bodyAsText()
        val data = readFromString(itemId, rawHtml)
        return Result.Success(data)
    }


    override suspend fun readFromString(
        itemId: Long,
        rawHtml: String?
    ): FullArticle = withContext(Dispatchers.IO) {
        // extract main text from raw html using essence's heuristics
        val htmlElement = rawHtml?.let { rh -> Essence.extract(rh).html }
        val html = htmlElement?.html()?:""
        val words = htmlElement?.text()?.split("\\s+".toRegex())?.filter { it.isNotBlank() }
        val wordCount = words?.size?.toLong() ?: 0L

        val applicationJson = rawHtml?.let {rh -> Jsoup.parse(rh) }
            ?.select("script[type=application/ld+json]")
            ?.map { script ->
                val json = script.data()
                val appJsonDto = AppJsonDto.decodeFromString(json)
                appJsonDto.clazz = script.attr("class")
                appJsonDto
            }

        val newsArticle = applicationJson
            ?.find { script -> script.type?.lowercase() == "newsarticle" }
            ?:applicationJson
                ?.filter { script -> script.graphs.isNotEmpty() }
                ?.map { script -> script.graphs.find { g -> g.type?.lowercase() == "newsarticle" } }
                ?.firstOrNull()

        val isFree = newsArticle?.isAccessibleForFree?:true

        val discussionUrl = newsArticle?.discussionUrl
        val commentCount = newsArticle?.commentCount?.toLong()?:0L

        val imageItems = applicationJson
            ?.filter { script -> script.type?.lowercase() == "imageobject" }
            ?.map { ao -> ao.toMediaItem() }
            ?:listOf()
        val audioItems = applicationJson
            ?.filter { script -> script.type?.lowercase() == "audioobject" }
            ?.map { ao -> ao.toMediaItem() }
            ?:listOf()
        val videoItems = applicationJson
            ?.filter { script -> script.type?.lowercase() == "videoobject" }
            ?.map { vo -> vo.toMediaItem() }
            ?:listOf()

        val imageDto = newsArticle
            ?.image
            ?.images
            ?.maxBy { image -> image.width ?: 0 }
        val articleImage = imageDto?.url?.firstOrNull()
            ?: imageDto?.contentUrl
            ?: imageItems.firstOrNull()?.url
            ?: ""

        FullArticle(
            itemId = itemId,
            applicationJson = applicationJson?.map { a -> a.toAppJson() }?:listOf(),
            html = html,
            imageItems = imageItems,
            videoItems = videoItems,
            audioItems = audioItems,
            articleImage = articleImage,
            discussionUrl = discussionUrl,
            commentCount = commentCount,
            isFree = isFree,
            wordCount = wordCount,
            readingTime = kotlin.math.ceil(wordCount.toDouble() / 225.0).roundToLong()
        )
    }
}
