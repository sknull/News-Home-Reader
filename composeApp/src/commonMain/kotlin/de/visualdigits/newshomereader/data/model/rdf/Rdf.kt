package de.visualdigits.newshomereader.data.model.rdf

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("RDF")
data class Rdf(
    @XmlElement(true) @XmlSerialName("channel") val channel: Channel? = null,
    @XmlElement(true) @XmlSerialName("image")val image: Image? = null,
    @XmlElement(true) @XmlSerialName("item") val items: List<Item>? = null
)
