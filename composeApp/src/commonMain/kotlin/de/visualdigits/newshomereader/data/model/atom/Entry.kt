package de.visualdigits.newshomereader.data.model.atom


import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class Entry(
    @XmlElement(true) @XmlSerialName("title") val title: String? = null,
    @XmlElement(true) @XmlSerialName("id") val id: String? = null,
    @XmlElement(true) @XmlSerialName("type") val type: String? = null,
    @XmlElement(true) @XmlSerialName("date") val date: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @XmlElement(true) @XmlSerialName("updated") val updated: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @XmlElement(true) @XmlSerialName("published") val published: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @XmlElement(true) @XmlSerialName("link") val link: Link? = null,
    @XmlElement(true) @XmlSerialName("author") val author: Author? = null,
    @XmlElement(true) @XmlSerialName("creator") val creator: String? = null,
    @XmlElement(true) @XmlSerialName("tags") val tags: String? = null,
    @XmlElement(true) @XmlSerialName("teasertyp") val teasertyp: String? = null,
    @XmlElement(true) @XmlSerialName("componentid") val componentid: String? = null,
    @XmlElement(true) @XmlSerialName("componenttitle") val componenttitle: String? = null,
    @XmlElement(true) @XmlSerialName("highlight") val highlight: String? = null,
    @XmlElement(true) @XmlSerialName("summary") val summary: String? = null,
    @XmlElement(true) @XmlSerialName("content") val content: String? = null
) {

    @XmlElement(true) val keywords: List<String>? = tags?.split(",")?.map { t -> t.trim() }?.filter { t -> t.isNotEmpty() }
}
