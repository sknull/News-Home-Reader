package de.visualdigits.newshomereader.domain.util

import org.jsoup.Jsoup
import java.io.File
import java.net.URI


fun extractImage(content: String): Triple<String?, String?, String?> {
    val document = Jsoup.parse(content)
    val image = document.select("img").firstOrNull()
    val url = image?.attr("src")
    val title = image?.attr("title")
    var caption = image?.attr("alt")
    if (caption?.isEmpty() == true) {
        caption = document.select("body").firstOrNull()?.wholeText()?.trim()
        if (caption?.isEmpty() == true) {
            caption = url?.let { u -> File(URI(u).path).nameWithoutExtension.replace("-", " ") }
        }
    }

    return Triple(url, title, caption)
}
