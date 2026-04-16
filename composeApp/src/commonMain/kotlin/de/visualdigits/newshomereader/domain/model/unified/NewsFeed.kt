package de.visualdigits.newshomereader.domain.model.unified

import java.time.OffsetDateTime

data class NewsFeed(
    val id: Long = 0,
    val identifier: String,
    val feedName: String,
    val title: String,
    val description: String,
    val link: String,
    val image: String,
    val imageTitle: String,
    val imageCaption: String,
    val updated: OffsetDateTime,
    val rights: String,
    val language: String,
    val keywords: List<String> = listOf(),

    val items: List<NewsItem> = listOf()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsFeed

        if (feedName != other.feedName) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = feedName.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }
}

