package de.visualdigits.newshomereader.data.model.rss

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("rss")
@Immutable
data class Rss(
    val version: String? = null,
    @XmlSerialName("channel") val channel: Channel? = null,
    @XmlElement(true) val about: String? = null,
    @XmlSerialName("item")val items: List<Item>? = null
)
