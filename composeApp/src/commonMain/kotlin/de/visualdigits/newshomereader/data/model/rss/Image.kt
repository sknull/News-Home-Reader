package de.visualdigits.newshomereader.data.model.rss

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
@Immutable
data class Image(
    @XmlElement(true) val about: String? = null,
    @XmlElement(true) val title: String? = null,
    @XmlElement(true) val description: String? = null,
    @XmlElement(true) val link: String? = null,
    @XmlElement(true) val url: String? = null,
    @XmlElement(true) val alt: String? = null,
    @XmlElement(true) val caption: String? = null,
    @XmlElement(true) val source: String? = null,
    @XmlElement(true) val data: String? = null,
    @XmlElement(true) val width: Int? = null,
    @XmlElement(true) val height: Int? = null
)
