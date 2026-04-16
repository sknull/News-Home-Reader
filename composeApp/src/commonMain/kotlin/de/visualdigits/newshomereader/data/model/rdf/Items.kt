package de.visualdigits.newshomereader.data.model.rdf

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Items(
    @XmlElement(true) @XmlSerialName("Seq") val seq: Seq
)
