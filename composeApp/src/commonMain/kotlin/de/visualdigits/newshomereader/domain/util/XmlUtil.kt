package de.visualdigits.newshomereader.domain.util

import de.visualdigits.newshomereader.domain.util.XmlUtil.xmlMapper
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

object XmlUtil {
    val builder = DefaultXmlSerializationPolicy.Builder()
    var xmlMapper: XML

    init {
        builder.pedantic = false
        builder.autoPolymorphic = true
        builder.unknownChildHandler = { _, _, _, _, _ -> listOf() }

        xmlMapper = XML {
            policy = DefaultXmlSerializationPolicy(builder)
        }
    }
}

/**
 * Removes all namespace information from the given xml to
 * prepare processing with kotlinx-serializable-xml.
 */
fun String.removeNamespaces(): String {
    return this
        .replace("<[a-zA-Z]+?:".toRegex(), "<")
        .replace("</[a-zA-Z]+?:".toRegex(), "</")
//        .replace("[a-zA-Z]+?:[a-zA-Z]+?=\"http.*?\"".toRegex(), "")
        .replace("xmlns=\"http.*?\"".toRegex(), "")
        .replace("\n +?>".toRegex(), ">")
        .replace(" +?>".toRegex(), ">")
        .replace("[a-zA-Z]+?:([a-zA-Z]+?)=".toRegex(), { match ->
            "${match.groups[1]?.value}="
        })
}

inline fun <reified T : Any> decodeValue(file: File): T {
    val rawXml = file.readText().removeNamespaces()
    return decodeFromString(rawXml)
}

@OptIn(ExperimentalXmlUtilApi::class)
inline fun <reified T : Any> decodeFromString(xml: String): T {
    return try {
        val rawXml = xml.removeNamespaces()
        xmlMapper.decodeFromString<T>(rawXml, null)
    } catch (e: Exception) {
        throw IllegalStateException("Could not parse string", e)
    }
}
