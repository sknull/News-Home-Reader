package de.visualdigits.newshomereader.data.model.newsfeeds

import org.junit.jupiter.api.Test
import java.io.File

class NewsFeedsTestConfiguration {

    @Test
    fun testReadModel() {
        val newsFeedConfiguration = NewsFeedConfigurationEntity.decodeValue(File(ClassLoader.getSystemResource("newsfeeds/newsfeeds.json").toURI()))
        println(newsFeedConfiguration)
    }
}
