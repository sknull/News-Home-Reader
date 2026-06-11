package de.visualdigits.newshomereader.domain.util

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Entities


object StringEscapeUtils {

    private val xmlSettings = Document.OutputSettings().apply {
        escapeMode(Entities.EscapeMode.xhtml) // xhtml entspricht der XML-Logik
    }

     fun escapeXml(xml: String): String = Entities.escape(xml, xmlSettings)

    fun unescapeXml(xml: String): String = Entities.unescape(xml)

    fun normalizeXml(xml: String): String = xml
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&", "&amp;")
}
