package de.visualdigits.newshomereader.data.model.rss

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
data class Comment(
    @XmlElement(true) val submitted: String? = null,
    @XmlElement(true) val title: String? = null,
    @XmlElement(true) val content: String? = null
)
