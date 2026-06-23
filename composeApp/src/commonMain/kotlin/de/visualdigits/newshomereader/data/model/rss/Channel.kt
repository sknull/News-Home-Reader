package de.visualdigits.newshomereader.data.model.rss

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class Channel(
    @XmlElement(false) val version: String? = null,
    @XmlElement(true) val title: String? = null,
    @XmlElement(true) val link: String? = null,
    @XmlElement(true) val category: String? = null,
    @XmlElement(true) val subject: String? = null,
    @XmlElement(true) val description: String? = null,
    @XmlElement(true) val source: String? = null,
    @XmlElement(true) val publisher: String? = null,
    @XmlElement(true) val rights: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("date") @XmlElement(true) val date: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlElement(true) val updatePeriod: String? = null,
    @XmlElement(true) val updateFrequency: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("updateBase") @XmlElement(true) val updateBase: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlElement(true) val broadcasting: String? = null,
    @XmlElement(true) @XmlSerialName("image") val image: Image? = null,
    @XmlElement(true) val language: String? = null,
    @XmlElement(true) val copyright: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("lastBuildDate") @XmlElement(true) val lastBuildDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("pubDate") @XmlElement(true) val pubDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @XmlElement(true) val docs: String? = null,
    @XmlElement(true) val ttl: Int? = null,
    @XmlElement(true) val itemRefs: List<String> = listOf(),
    @XmlElement(true) @XmlSerialName("item") val items: List<Item>? = null
)
