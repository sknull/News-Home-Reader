package de.visualdigits.newshomereader.data.model.rdf

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.domain.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.time.OffsetDateTime

@Serializable
@Immutable
data class Channel(
    @XmlElement(false) @XmlSerialName("about") val about: String? = null,
    @XmlElement(true) @XmlSerialName("title") val title: String? = null,
    @XmlElement(true) @XmlSerialName("link") val link: String? = null,
    @XmlElement(true) @XmlSerialName("description") val description: String? = null,
    @XmlElement(true) @XmlSerialName("language") val language: String? = null,
    @XmlElement(true) @XmlSerialName("copyright") val copyright: String? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("lastBuildDate") @XmlElement(true) val lastBuildDate: OffsetDateTime = OffsetDateTime.now(),
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("pubDate") @XmlElement(true) val pubDate: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) @XmlSerialName("ttl") val ttl: Int? = null,
    @XmlElement(true) @XmlSerialName("image") val image: Image? = null,
    @XmlElement(true) @XmlSerialName("publisher") val publisher: String? = null,
    @XmlElement(true) @XmlSerialName("rights") val rights: String? = null,
    @XmlElement(true) @XmlSerialName("date") val date: String? = null,
    @XmlElement(true) @XmlSerialName("source") val source: String? = null,
    @XmlElement(true) @XmlSerialName("updatePeriod") val updatePeriod: String? = null,
    @XmlElement(true) @XmlSerialName("updateFrequency") val updateFrequency: String? = null,
    @XmlElement(true) @XmlSerialName("updateBase") val updateBase: String? = null,
    @XmlElement(true) @XmlSerialName("items") val items: List<Items>? = null
)
