package de.visualdigits.newshomereader.data.model.atom

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
data class Link(
    @XmlElement(false) val rel: String? = null,
    @XmlElement(false) val type: String? = null,
    @XmlElement(false) val href: String? = null
)
