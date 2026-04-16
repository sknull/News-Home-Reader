package de.visualdigits.newshomereader.data.model.opml

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
@XmlSerialName("opml")
data class Opml(
    @XmlElement(false) val version: String? = null,
    @XmlSerialName("head") val head: Head? = null,
    @XmlSerialName("body") val body: Body? = null
)
