package de.visualdigits.newshomereader.data.model.atom


import de.visualdigits.newshomereader.data.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.time.OffsetDateTime

@Serializable
data class Entry(
    @XmlElement(true) @XmlSerialName("title") val title: String? = null,
    @XmlElement(true) @XmlSerialName("id") val id: String? = null,
    @XmlElement(true) @XmlSerialName("type") val type: String? = null,
    @XmlElement(true) @XmlSerialName("date") @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) val date: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) @XmlSerialName("updated") @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) val updated: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) @XmlSerialName("published") @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) val published: OffsetDateTime = OffsetDateTime.now(),
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
