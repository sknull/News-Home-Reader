package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import com.fleeksoft.ksoup.Ksoup
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.essence.Essence
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.mapper.toFullArticle
import de.visualdigits.newshomereader.data.database.mapper.toFullArticleEntity
import de.visualdigits.newshomereader.data.database.upsertFullArticle
import de.visualdigits.newshomereader.data.mapper.toAppJson
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonWrapper
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToLong

open class DefaultArticleRepository(
    private val httpClient: HttpClient,
    private val dao: NewsHomeReaderDatabaseQueries? = null
) : ArticleRepository {

    val log = Logger.withTag("DefaultArticleRepository")

    override suspend fun readFromFile(
        newsItem: NewsItem,
        file: File
    ): FullArticle = withContext(Dispatchers.IO) {
        readFromString(newsItem, file.readText())
    }

    override suspend fun getFullArticle(itemId: Long): Result<FullArticle?, DataError.Local> {
        return try {
            Result.Success(dao!!.getFullArticleByItemId(itemId).executeAsOneOrNull()?.toFullArticle())
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, throwable = e)
        }
    }

    override suspend fun readFullArticle(
        newsItem: NewsItem
    ): Result<Pair<FullArticle, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        try {
            var fullArticle = dao!!.getFullArticleByItemId(newsItem.id).executeAsOneOrNull()?.toFullArticle()
            val changedArticle = if (fullArticle == null || newsItem.isChanged) {
                val response = httpClient.get(urlString = newsItem.link)
                val rawHtml = response.bodyAsText()
                fullArticle = readFromString(newsItem, rawHtml, newsItem.link)
                dao.upsertFullArticle(fullArticle.toFullArticleEntity())
            } else {
                false
            }
            Result.Success(Pair(fullArticle, changedArticle))
        } catch (e: Exception) {
            Result.Error(DataError.Remote.SERIALIZATION, throwable = e)
        }
    }

    override suspend fun readFromString(
        newsItem: NewsItem,
        rawHtml: String?,
        url: String?
    ): FullArticle = withContext(Dispatchers.IO) {
        // extract main text from raw html using essence's heuristics
        val htmlElement = rawHtml?.let { rh -> Essence.extract(rh).html }
        var html = htmlElement?.html()?:""

        val words = htmlElement?.text()?.split("\\s+".toRegex())?.filter { it.isNotBlank() }
        val wordCount = words?.size?.toLong() ?: 0L

        val applicationJson = try {
            rawHtml?.let { rh -> Ksoup.parse(rh) }
                ?.select("script[type=application/ld+json]")
                ?.flatMap { script ->
                    val json = script.data()
                    AppJsonWrapper.decodeFromString(json).appJsons.map { appJsonDto ->
                        appJsonDto.clazz = script.attr("class")
                        appJsonDto
                    }
                }?:listOf()
        } catch (e: Exception) {
            log.e("Could not parse app json for article url: $url", e)
            listOf()
        }

        val newsArticle = applicationJson
            .find { script -> script.type?.lowercase()?.contains("newsarticle") == true }
            ?:applicationJson
                .filter { script -> script.graphs.isNotEmpty() }
                .map { script -> script.graphs.find { g -> g.type?.lowercase()?.contains("newsarticle") == true } }
                .firstOrNull()

        val isFree = newsArticle?.isAccessibleForFree?:true

        val discussionUrl = newsArticle?.discussionUrl
        val commentCount = newsArticle?.commentCount?.toLong()?:0L

        val imageItems = applicationJson
            .filter { script -> script.type?.lowercase() == "imageobject" }
            .map { ao -> ao.toMediaItem() }
        val audioItems = applicationJson
            .filter { script -> script.type?.lowercase() == "audioobject" }
            .map { ao -> ao.toMediaItem() }
        val videoItems = applicationJson
            .filter { script -> script.type?.lowercase() == "videoobject" }
            .map { vo -> vo.toMediaItem() } +
                applicationJson.flatMap { aj ->
                    aj.video?.videos?.map { video -> video.toMediaItem() }?:listOf()
                }

        val imageDto = newsArticle
            ?.image
            ?.images
            ?.maxBy { image -> image.width ?: 0 }
        val articleImage = imageDto?.url?.firstOrNull()
            ?: imageDto?.contentUrl
            ?: imageItems.firstOrNull()?.url
            ?: ""

        // cleanup if possible
        if (html.contains(newsItem.summary)) {
            html = html.replace(newsItem.summary, "")
        }

        FullArticle(
            id = 0L,
            itemId = newsItem.id,
            applicationJson = applicationJson.map { a -> a.toAppJson() }?:listOf(),
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
