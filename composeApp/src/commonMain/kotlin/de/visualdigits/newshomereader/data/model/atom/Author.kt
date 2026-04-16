package de.visualdigits.newshomereader.data.model.atom

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
data class Author(
    @XmlElement(true) val name: String? = null,
    @XmlElement(true) val uri: String? = null
)
