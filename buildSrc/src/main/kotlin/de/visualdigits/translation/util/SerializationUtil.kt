package de.visualdigits.translation.util

import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

inline fun <reified T> T.writeValueAsXmlString(
    indent: Boolean = true,
    writeXmlDeclaration: Boolean = true,
    expandSelfClosingTags: Boolean = false
): String {
    val rawXml = XML.v1 {
        xmlDeclMode = if (writeXmlDeclaration) XmlDeclMode.Charset else XmlDeclMode.None
        xmlVersion = XmlVersion.XML10
        indentString = if (indent) "  " else ""
//            policy = DefaultXmlSerializationPolicy(
//                pedantic = false,
//                autoPolymorphic = true
//            )
    }.encodeToString(this, null)

    val finalXml = if (expandSelfClosingTags) {
        rawXml.replace(Regex("""<([\w:]+)([^>]*)\s*/>""")) { match ->
            val tagName = match.groupValues[1]
            val attributes = match.groupValues[2]
            "<$tagName$attributes></$tagName>"
        }
    } else {
        rawXml
    }

    return finalXml.replace("\n", "\r\n")
}

inline fun <reified T> T.writeValueAsXmlFile(
    file: File, indent: Boolean = true,
    writeXmlDeclaration: Boolean = true,
    expandSelfClosingTags: Boolean = false
) {
    file.writeText(writeValueAsXmlString(indent, writeXmlDeclaration, expandSelfClosingTags))
}
