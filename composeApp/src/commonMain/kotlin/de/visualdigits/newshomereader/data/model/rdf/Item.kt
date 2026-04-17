package de.visualdigits.newshomereader.data.model.rdf

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import org.apache.commons.text.StringEscapeUtils
import java.time.OffsetDateTime

@Serializable
@Immutable
data class Item(
    @XmlElement(false) @XmlSerialName("about") val about: String? = null,
    @XmlElement(true) @XmlSerialName("title") val title: String? = null,
    @XmlElement(true) @XmlSerialName("link") val link: String? = null,
    @XmlElement(true) @XmlSerialName("language") val language: String? = null,
    @XmlElement(true) @XmlSerialName("description") val description: String? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlElement(true) @XmlSerialName("pubDate") val pubDate: OffsetDateTime = OffsetDateTime.now(), // update date time or first publish date time when date is empty
    @XmlElement(true) @XmlSerialName("guid") val guid: String? = null,
    @XmlElement(true) @XmlSerialName("image") val images: List<Image> = listOf(),
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlElement(true) @XmlSerialName("date") val date: OffsetDateTime = OffsetDateTime.now(), // first publish date time
    @XmlElement(true) @XmlSerialName("encoded") var encoded: String? = null,
    @XmlElement(true) @XmlSerialName("format") val format: String? = null,
    @XmlElement(true) @XmlSerialName("rights") val rights: String? = null,
    @XmlElement(true) @XmlSerialName("publisher") val publisher: String? = null,
    @XmlElement(true) @XmlSerialName("identifier") val identifier: String? = null,
    @XmlElement(true) @XmlSerialName("subjects") val subjects: String? = null,
    @XmlElement(true) @XmlSerialName("audience") val audience: String? = null,
    @XmlElement(true) @XmlSerialName("isFormatOf") val isFormatOf: String? = null,
) {

    val content: String?
        get() {
            return encoded?.let { e -> StringEscapeUtils.unescapeXml(e) }
        }
}
