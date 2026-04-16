package de.visualdigits.newshomereader.data.model.opml

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
data class Outline(
    @XmlElement(false) val title: String? = null,
    @XmlElement(false) val text: String? = null,
    @XmlElement(false) val type: String? = null,

    @XmlElement(false) val notify: String? = null,
    @XmlElement(false) val imageUrl: String? = null,
    @XmlElement(false) val fullTextByDefault: String? = null,
    @XmlElement(false) val openArticlesWith: String? = null,
    @XmlElement(false) val alternateId: String? = null,
    @XmlElement(false) val xmlUrl: String? = null,

    @XmlSerialName("outline") val outlines: List<Outline> = listOf()
)
