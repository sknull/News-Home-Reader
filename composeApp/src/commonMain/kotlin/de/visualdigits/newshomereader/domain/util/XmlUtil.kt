package de.visualdigits.newshomereader.domain.util

import de.visualdigits.newshomereader.domain.util.XmlUtil.xmlMapper
import kotlinx.serialization.SerializationStrategy
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.SimpleNamespaceContext
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File
import java.io.OutputStream

object XmlUtil {

    var xmlMapper: XML

    init {
        val builder = DefaultXmlSerializationPolicy.Builder()
        builder.pedantic = false
        builder.autoPolymorphic = true
        builder.unknownChildHandler = { _, _, _, _, _ -> listOf() }

        xmlMapper = XML {
            xmlVersion = XmlVersion.XML11
            xmlDeclMode = XmlDeclMode.Charset
            indent = 4
            repairNamespaces = true
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

inline fun <reified T : Any> decodeValue(file: File, removeNamespaces: Boolean = true): T {
    val rawXml = file.readText()
    return decodeFromString(rawXml, removeNamespaces)
}

@OptIn(ExperimentalXmlUtilApi::class)
inline fun <reified T : Any> decodeFromString(xml: String, removeNamespaces: Boolean = true): T {
    return try {
        val rawXml = if (removeNamespaces) xml.removeNamespaces() else xml
        xmlMapper.decodeFromString<T>(rawXml, null)
    } catch (e: Exception) {
        throw IllegalStateException("Could not parse string", e)
    }
}

@OptIn(ExperimentalXmlUtilApi::class)
fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String  {
    return try {
        xmlMapper.encodeToString(serializer, value)
    } catch (e: Exception) {
        throw IllegalStateException("Could not parse string", e)
    }
}
