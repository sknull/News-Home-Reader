package de.visualdigits.newshomereader.data.model.atom


import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Text(
    @XmlElement(false) val type: String? = null,
    @XmlValue val text: String
)
