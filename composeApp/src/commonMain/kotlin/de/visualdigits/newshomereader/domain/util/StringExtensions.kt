package de.visualdigits.newshomereader.domain.util

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
