package de.visualdigits.newshomereader.data.model.newsfeeds

import de.visualdigits.newshomereader.domain.model.unified.NewsFeedConfiguration
import org.junit.jupiter.api.Test
import java.io.File

class NewsFeedsTestConfiguration {

    @Test
    fun testReadModel() {
        val newsFeedConfiguration = NewsFeedConfiguration.decodeValue(File(ClassLoader.getSystemResource("newsfeeds/newsfeeds.json").toURI()))
        println(newsFeedConfiguration)
    }
}
