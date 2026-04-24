package de.visualdigits.newshomereader.data.model.opml

import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.mapper.toOpml
import de.visualdigits.newshomereader.domain.util.decodeValue
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test
import java.io.File

class OpmlTest {

    @Test
    fun testReadModel() {
//        val opml1 = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-26-65586.opml").toURI()))
//        val opml3 = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feedflow-export_24-5-2025_samsung-sm-t720.opml").toURI()))
//        val opml = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-28-42199.opml").toURI()), false)
        val opml = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/newshomereader-export_2026-04-22_12-15-31.opml").toURI()), false)
        val newsFeeds = opml.toNewsFeedConfiguration()
        val opml2 = newsFeeds.toOpml()

        val format = XML {
            xmlVersion = XmlVersion.XML11
            xmlDeclMode = XmlDeclMode.Charset
            indent = 4
        }

        val xml = format.encodeToString(Opml.serializer(), opml)
        val xml2 = format.encodeToString(Opml.serializer(), opml2)

        println(newsFeeds)
    }
}
