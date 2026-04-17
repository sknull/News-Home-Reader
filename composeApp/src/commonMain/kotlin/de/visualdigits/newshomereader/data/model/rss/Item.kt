package de.visualdigits.newshomereader.data.model.rss

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
    @XmlElement(true) @XmlSerialName("guid") val guid: Guid? = null,
    @XmlElement(true) val identifier: String? = null,
    @XmlElement(true) val id: String? = null,

    @XmlElement(true) @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("date") val date: OffsetDateTime = OffsetDateTime.now(), // first publish date time
    @XmlElement(true) @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("pubDate") val pubDate: OffsetDateTime = OffsetDateTime.now(), // update date time or first publish date time when date is empty

    @XmlElement(true) val about: String? = null,
    @XmlElement(true) val creator: String? = null,
    @XmlElement(true) val type: String? = null,
    @XmlElement(true) val format: String? = null,
    @XmlElement(true) val source: String? = null,
    @XmlElement(true) val language: String? = null,
    @XmlElement(true) val publisher: String? = null,
    @XmlElement(true) val rights: String? = null,
    @XmlElement(true) val subject: String? = null,
    @XmlElement(true) val audience: String? = null,
    @XmlElement(true) val isFormatOf: String? = null,
    @XmlElement(true) @XmlSerialName("encoded") var encoded: String? = null,
    @XmlElement(true) val topline: String? = null,
    @XmlElement(true) val states: String? = null,

    @XmlElement(true) val title: String? = null,
    @XmlElement(true) val link: String? = null,
    @XmlElement(true) val description: String? = null,
    @XmlElement(true) @XmlSerialName("category") val categories: List<de.visualdigits.newshomereader.data.model.atom.Text> = listOf(),
    @XmlElement(true) val isPermaLink: Boolean? = null,
    @XmlElement(true) @XmlSerialName("enclosure")val enclosure: Enclosure? = null,
    @XmlElement(true) @XmlSerialName("image")val images: List<Image> = listOf(),

    @XmlElement(true) @XmlSerialName("comment") val comments: MutableList<Comment> = mutableListOf(),
) {

    val content: String?
        get() {
            return encoded?.let { e -> StringEscapeUtils.unescapeXml(e) }
        }
}
