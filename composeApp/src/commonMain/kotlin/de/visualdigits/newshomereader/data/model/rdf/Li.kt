package de.visualdigits.newshomereader.data.model.rdf

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Li(
    @XmlElement(false) @XmlSerialName("resource") val resource: String? = null
)
