package de.visualdigits.newshomereader.data.model.rdf

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class Channel(
    @XmlElement(false) @XmlSerialName("about") val about: String? = null,
    @XmlElement(true) @XmlSerialName("title") val title: String? = null,
    @XmlElement(true) @XmlSerialName("link") val link: String? = null,
    @XmlElement(true) @XmlSerialName("description") val description: String? = null,
    @XmlElement(true) @XmlSerialName("language") val language: String? = null,
    @XmlElement(true) @XmlSerialName("copyright") val copyright: String? = null,
    @XmlSerialName("lastBuildDate") @XmlElement(true) val lastBuildDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlSerialName("pubDate") @XmlElement(true) val pubDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
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
