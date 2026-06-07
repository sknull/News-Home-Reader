package de.visualdigits.newshomereader.domain.util

private val P_DURATION = "(\\d+?)M(\\d+?)S".toRegex()
private val P_MINUTES = "(\\d+?)M".toRegex()
private val P_SECONDS = "(\\d+?)S".toRegex()

/**
 * extract domain for googles favicon service
 */
fun String.getFaviconUrl(sizePx: Int): String {
    val domain = this
        .substringAfter("://")   // Entfernt http:// oder https://
        .substringBefore("/")    // Entfernt alle Pfade am Ende
        .removePrefix("www.")    // Optional, aber macht die Domain sauberer

    return "https://www.google.com/s2/favicons?domain=${domain}&sz=${sizePx}"
}

fun String.parseDuration(): String {
    val s = replace("PT", "")
    val matchDuration = P_DURATION.find(s)
    val d = if (matchDuration != null) {
        val m = matchDuration.groups[1]?.value?.padStart(2, '0')?:"00"
        val s = matchDuration.groups[2]?.value?.padStart(2, '0')?:"00"
        "$m:$s"
    } else {
        val matchMinutes = P_MINUTES.find(s)
        if (matchMinutes != null) {
            val m = matchMinutes.groups[1]?.value?.padStart(2, '0')?:"00"
            "$m:00"
        } else {
            val matchSeconds = P_SECONDS.find(s)
            if (matchSeconds != null) {
                val s = matchSeconds.groups[1]?.value?.padStart(2, '0')?:"00"
                "00:$s"
            } else {
                ""
            }
        }
    }

    return d
}
