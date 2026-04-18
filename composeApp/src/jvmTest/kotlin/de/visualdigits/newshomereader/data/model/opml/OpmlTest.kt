package de.visualdigits.newshomereader.data.model.opml

import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.domain.util.decodeValue
import org.junit.jupiter.api.Test
import java.io.File

class OpmlTest {

    @Test
    fun testReadModel() {
//        val opml1 = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-26-65586.opml").toURI()))
//        val opml3 = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feedflow-export_24-5-2025_samsung-sm-t720.opml").toURI()))

        val opml = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-28-42199.opml").toURI()))
        opml.toNewsFeedConfiguration()
        println()
    }
}
