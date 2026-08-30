package de.visualdigits.newshomereader.data.repository

import co.touchlab.kermit.Logger
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.essence.Essence
import de.visualdigits.newshomereader.NewsHomeReaderDatabaseQueries
import de.visualdigits.newshomereader.data.database.toFullArticle
import de.visualdigits.newshomereader.data.database.toFullArticleEntity
import de.visualdigits.newshomereader.data.database.upsertFullArticle
import de.visualdigits.newshomereader.data.mapper.toAppJson
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonWrapper
import de.visualdigits.newshomereader.data.model.youtube.OEmbed
import de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer.HrMediaPlayerLoader
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.HtmlElement
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.model.unified.ThumbnailItem
import de.visualdigits.newshomereader.domain.repository.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.math.ceil
import kotlin.math.roundToLong

open class DefaultArticleRepository(
    private val httpClient: HttpClient,
    private val dao: NewsHomeReaderDatabaseQueries
) : ArticleRepository {

    val log = Logger.withTag("DefaultArticleRepository")

    private val articleLocks = ConcurrentMap<Long, Mutex>()

    companion object {
        private val jsonMapper = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    override suspend fun getFullArticle(itemId: Long): Result<FullArticle?, DataError.Local> {
        return try {
            Result.Success(dao.getFullArticleByItemId(itemId).executeAsOneOrNull()?.toFullArticle())
        } catch (e: Exception) {
            Result.Error(DataError.Local.SERIALIZATION, throwable = e)
        }
    }

    override suspend fun readFullArticle(
        newsItem: NewsItem
    ): Result<Pair<FullArticle?, Boolean>, DataError.Remote> = withContext(Dispatchers.IO) {
        val lock = articleLocks.computeIfAbsent(newsItem.id) { Mutex() }
        lock.withLock {
            try {
                var fullArticle = dao.getFullArticleByItemId(newsItem.id).executeAsOneOrNull()?.toFullArticle()
                val changedArticle = if (fullArticle == null || newsItem.isChanged || (fullArticle.html.isEmpty() && fullArticle.retries < 3)) {
                    val response = withContext(Dispatchers.IO + NonCancellable) {
                        httpClient.get(urlString = newsItem.link)
                    }
                    val readFullArticle = if (response.status.value == 200) {
                        readFromString(newsItem, response.bodyAsText(), newsItem.link)
                    } else {
                        null
                    }
                    if (readFullArticle != null) {
                        if (fullArticle == null) {
                            fullArticle = readFullArticle
                        } else {
                            fullArticle = fullArticle.copy(
                                applicationJson = readFullArticle.applicationJson,
                                html = readFullArticle.html,
                                parts = readFullArticle.parts,
                                imageItems = readFullArticle.imageItems,
                                videoItems = readFullArticle.videoItems,
                                audioItems = readFullArticle.audioItems,
                                articleImage = readFullArticle.articleImage,
                                discussionUrl = readFullArticle.discussionUrl,
                                commentCount = readFullArticle.commentCount,
                                isPaid = readFullArticle.isPaid,
                                wordCount = readFullArticle.wordCount,
                                readingTime = readFullArticle.readingTime,
                                retries = fullArticle.retries + 1
                            )
                        }
                    } else if (fullArticle != null){
                        fullArticle = fullArticle.copy(
                            retries = fullArticle.retries + 1
                        )
                    }
                    if (fullArticle != null) {
                        dao.upsertFullArticle(fullArticle.toFullArticleEntity())
                    } else {
                        false
                    }
                } else {
                    false
                }
                Result.Success(Pair(fullArticle, changedArticle))
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION, throwable = e)
            } finally {
                if (lock.tryLock()) {
                    try {
                        articleLocks.remove(newsItem.id)
                    } finally {
                        lock.unlock()
                    }
                }
            }
        }
    }

    override suspend fun readFromString(
        newsItem: NewsItem?,
        rawHtml: String,
        url: String?
    ): FullArticle = withContext(Dispatchers.IO) {
        // extract main text from raw html using essence's heuristics
        val (htmlElement, parts) = rawHtml.let { rh ->
            val result = Essence.extract(rh)
            result.html to result.parts.mapNotNull { part ->
                when (part.tagName()) {
                    "div" -> HtmlElement(
                        tagName = part.tagName(),
                        html = part.html()
                    )
                    "img" -> HtmlElement(
                        tagName = part.tagName(),
                        href = part.attr("src"),
                        alt = if (part.hasAttr("alt")) part.attr("alt") else part.attr("title")
                    )
                    else -> null
                }
            }
        }
        var html = htmlElement.html()

        val words = htmlElement.text().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size.toLong() ?: 0L

        val applicationJson = try {
            rawHtml.let { rh -> Ksoup.parse(rh) }
                .select("script[type=application/ld+json]")
                .flatMap { script ->
                    val json = script.data()
                    AppJsonWrapper.decodeFromString(json).appJsons.map { appJsonDto ->
                        appJsonDto.clazz = script.attr("class")
                        appJsonDto
                    }
                }
        } catch (e: Exception) {
            Logger.e("Could not parse app json for article url: $url", e)
            listOf()
        }

        val newsArticle = applicationJson
            .find { script -> script.type?.lowercase()?.contains("newsarticle") == true }
            ?:applicationJson
                .filter { script -> script.graphs.isNotEmpty() }
                .map { script -> script.graphs.find { g -> g.type?.lowercase()?.contains("newsarticle") == true } }
                .firstOrNull()

        val isPaid = !(newsArticle?.isAccessibleForFree?:true)

        val discussionUrl = newsArticle?.discussionUrl
        val commentCount = newsArticle?.commentCount?.toLong()?:0L

        val document = Ksoup.parse(html = rawHtml)

        val imageItems = applicationJson
            .filter { script -> script.type?.lowercase() == "imageobject" }
            .map { ao -> ao.toMediaItem() }

        //
        // HR On Demand Audios
        //
        val hrAudios = scrapeHrMedia(document, "audio/mp3")

        val audioItems = applicationJson
            .filter { script -> script.type?.lowercase() == "audioobject" }
            .map { ao -> ao.toMediaItem() }

        //
        // Youtube Videos
        //

        // scrape from inline player
        val youtubeVideos1 = withContext(Dispatchers.IO + NonCancellable) {
            document.select("lite-youtube")
                .mapNotNull { elem ->
                    val videoId = elem.attr("videoid")
                    if (videoId.isNotEmpty()) {
                        scrapeYoutubeVideo("https://www.youtube.com/watch?v=$videoId")
                    } else null
                }.awaitAll()
                .filterNotNull()
        }

        // scrape from links
        val youtubeVideos2 = withContext(Dispatchers.IO + NonCancellable) {
            document.select("a[href^=https://www.youtube.com/watch?v=]")
                .mapNotNull { elem ->
                    scrapeYoutubeVideo(elem.attr("href"))
                }.awaitAll()
                .filterNotNull()
        }

        //
        // HR On Demand Videos
        //
        val hrVideos = scrapeHrMedia(document, "video/mp4")

        //
        // From application json
        //
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
        newsItem?.summary?.also { s ->
            if (html.contains(s)) {
                html = html.replace(s, "")
            }
        }

        FullArticle(
            id = 0L,
            itemId = newsItem?.id?:0L,
            applicationJson = applicationJson.map { a -> a.toAppJson() },
            html = html,
            parts = parts,
            videoItems = videoItems + youtubeVideos1 + youtubeVideos2 + hrVideos,
            audioItems = audioItems + hrAudios,
            imageItems = imageItems,
            articleImage = articleImage,
            discussionUrl = discussionUrl,
            commentCount = commentCount,
            isPaid = isPaid,
            wordCount = wordCount,
            readingTime = ceil(wordCount.toDouble() / 225.0).roundToLong()
        )
    }

    private fun CoroutineScope.scrapeYoutubeVideo(videoUrl: String): Deferred<MediaItem?>? = if (videoUrl.isNotEmpty()) {
        async {
            try {
                val embedUrl = "https://www.youtube.com/oembed?url=$videoUrl&format=json"
                val json = httpClient.get(embedUrl).bodyAsText()
                Json.decodeFromString<OEmbed>(json).toMediaItem(videoUrl)
            } catch (_: Exception) {
                null
            }
        }
    } else null

    private fun scrapeHrMedia(document: Document, mimeType: String): List<MediaItem> {
        val onDemandVideos = document
            .select("div[x-show=avStart]")
            .map {
                val json = it.attr("data-hr-mediaplayer-loader").replace("&quot", "\"")
                val config = jsonMapper.decodeFromString<HrMediaPlayerLoader>(json)
                val url = config.mediaCollection?.streams
                    ?.flatMap { s ->
                        s.media
                            .filter { m -> m.mimeType == mimeType }
                            .mapNotNull { m -> m.url }
                            .sortedByDescending { url ->
                                url
                                    .substringAfterLast('_')
                                    .substringBefore('-')
                                    .substringBefore('x')
                                    .toInt()
                            }
                    }?.firstOrNull()
                val size = config.playerConfig?.generic?.imageTemplateConfig?.size?.sortedByDescending { size -> size.minWidth }?.firstOrNull()?.value
                val meta = config.mediaCollection?.meta
                MediaItem(
                    url = url,
                    duration = "${meta?.durationSeconds}S",
                    headline = meta?.title,
                    description = meta?.title,
                    thumbnails = size?.let {
                        meta?.images?.map { image ->
                            ThumbnailItem(
                                url = image.url?.let { url -> listOf(url.replace("{size}", size)) } ?: listOf(),
                                author = config.playerConfig.pluginData?.trackingPianoall?.avContent?.avContentTheme1,
                            )
                        }
                    }  ?: listOf()
                )
            }
        return onDemandVideos
    }
}
