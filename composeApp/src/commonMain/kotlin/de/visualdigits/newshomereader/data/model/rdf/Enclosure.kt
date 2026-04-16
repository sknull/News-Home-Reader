package de.visualdigits.newshomereader.data.model.rdf

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
data class Enclosure(
    @XmlElement(false) val `type`: String? = null,
    @XmlElement(false) val length: Int? = null,
    @XmlElement(false) val url: String? = null
)
