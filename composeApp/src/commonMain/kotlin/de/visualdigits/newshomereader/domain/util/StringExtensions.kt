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
        val m = matchDuration.groups[1]?.value?.toInt() ?: 0
        val s = matchDuration.groups[2]?.value?.toInt() ?: 0
        val ts = m * 60 + s
        val vh = (ts / 3600).toString().padStart(2, '0')
        val rest = ts % 3600
        val vm = (rest / 60).toString().padStart(2, '0')
        val vs = (rest % 60).toString().padStart(2, '0')
        "$vh:$vm:$vs"
    } else {
        val matchMinutes = P_MINUTES.find(s)
        if (matchMinutes != null) {
            val m = matchMinutes.groups[1]?.value?.padStart(2, '0')?.toInt()?:0
            if (m > 60) {
                val vh = (m / 60).toString().padStart(2, '0')
                val vm = (m % 60).toString().padStart(2, '0')
                "$vh:$vm:00"
            } else {
                "00:${m.toString().padStart(2, '0')}:00"
            }
        } else {
            val matchSeconds = P_SECONDS.find(s)
            if (matchSeconds != null) {
                val s = matchSeconds.groups[1]?.value?.padStart(2, '0')?.toInt()?:0
                if (s > 3600) {
                    val vh = (s / 3600).toString().padStart(2, '0')
                    val rest = s % 3600
                    val vm = (rest / 60).toString().padStart(2, '0')
                    val vs = (rest % 60).toString().padStart(2, '0')
                    "$vh:$vm:$vs"
                } else if (s > 60) {
                    val vm = (s / 60).toString().padStart(2, '0')
                    val vs = (s % 60).toString().padStart(2, '0')
                    "00:$vm:$vs"
                } else {
                    "00:00:${s.toString().padStart(2, '0')}"
                }
            } else {
                "00:00:00"
            }
        }
    }

    return d
}
