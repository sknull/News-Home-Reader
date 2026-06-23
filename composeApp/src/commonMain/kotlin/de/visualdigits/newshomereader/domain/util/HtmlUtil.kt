package de.visualdigits.newshomereader.domain.util

import com.fleeksoft.ksoup.Ksoup
import io.ktor.http.Url
import io.ktor.http.fullPath


fun extractImage(content: String): Triple<String?, String?, String?> {
    val document = Ksoup.parse(content)
    val image = document.select("img").firstOrNull()
    val url = image?.attr("src")
    val title = image?.attr("title")
    var caption = image?.attr("alt")
    if (caption?.isEmpty() == true) {
        caption = document.select("body").firstOrNull()?.wholeText()?.trim()
        if (caption?.isEmpty() == true) {
            caption = url?.let { u -> Url(u).fileNameWithoutExtension().replace("-", " ") }
        }
    }
    return Triple(url, title, caption)
}

fun Url.fileName(): String = fullPath.substringAfterLast('/').substringBeforeLast('?')

fun Url.fileNameWithoutExtension(): String = fileName().substringBeforeLast('.')
