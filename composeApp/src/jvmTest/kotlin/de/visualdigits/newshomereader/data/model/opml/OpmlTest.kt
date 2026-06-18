package de.visualdigits.newshomereader.data.model.opml

import de.visualdigits.newshomereader.data.mapper.toNewsFeedConfiguration
import de.visualdigits.newshomereader.data.mapper.toOpml
import de.visualdigits.newshomereader.domain.mapper.mergeNewsFeedGroups
import de.visualdigits.newshomereader.domain.util.decodeValue
import de.visualdigits.newshomereader.domain.util.encodeToString
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class OpmlTest {

    @Test
    fun testReadModel() {
//        val opml1 = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-26-65586.opml").toURI()))
//        val opml3 = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feedflow-export_24-5-2025_samsung-sm-t720.opml").toURI()))
//        val opml = decodeValue<Opml>(File(ClassLoader.getSystemResource("opml/feeder-export-2025-11-28-42199.opml").toURI()), false)
        val opml = decodeValue<Opml>(
            File(
                ClassLoader.getSystemResource("opml/newshomereader-export_2026-05-09_17-46-33.opml").toURI()
            ), false
        )
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


    @Test
    fun testMerge() {
        val opmlExisting = decodeValue<Opml>(
            File(ClassLoader.getSystemResource("opml/newshomereader-export_existing.opml").toURI()),
            false
        )
        val newsFeedsExisting = opmlExisting.toNewsFeedConfiguration()

        val opmlToMerge = decodeValue<Opml>(
            File(ClassLoader.getSystemResource("opml/newshomereader-export_tomerge.opml").toURI()),
            false
        )
        val newsFeedsToMerge = opmlToMerge.toNewsFeedConfiguration()

        val newsFeedsMerged = newsFeedsExisting.mergeNewsFeedGroups(newsFeedsToMerge)
        val opmlActual = encodeToString(Opml.serializer(), newsFeedsMerged.toOpml())

        val opmlExpected = File(ClassLoader.getSystemResource("opml/newshomereader-export_expected.opml").toURI())
            .readText()
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .trim()

        assertEquals(opmlExpected, opmlActual)
    }
}

