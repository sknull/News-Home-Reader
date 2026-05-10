package de.visualdigits.newshomereader.data.model.atom

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.domain.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.time.OffsetDateTime


@Serializable
@Immutable
data class Feed(
    @XmlElement(true) @XmlSerialName("title") val title: String? = null,
    @XmlElement(true) @XmlSerialName("subtitle") val subtitle: Text? = null,
    @XmlElement(true) @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("date") val date: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("updated") val updated: OffsetDateTime = OffsetDateTime.now(),
    @XmlElement(true) @XmlSerialName("id") val id: String? = null,
    @XmlElement(true) @XmlSerialName("author") val author: Author? = null,
    @XmlElement(true) @XmlSerialName("link") val links: List<Link>? = null,
    @XmlElement(true) @XmlSerialName("rights") val rights: String? = null,
    @XmlElement(true) @XmlSerialName("tags") val tags: String? = null,
    @XmlElement(true) @XmlSerialName("entry") val entries: List<Entry>? = null
) {

    val keywords: List<String>? = tags?.split(",")?.map { t -> t.trim() }?.filter { t -> t.isNotEmpty() }
}
