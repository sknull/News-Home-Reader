package de.visualdigits.newshomereader.data.model.opml

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlNamespaceDeclSpecs
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@OptIn(ExperimentalXmlUtilApi::class)
@Serializable
@XmlSerialName("opml")
@XmlNamespaceDeclSpecs(
    "feeder=https://nononsenseapps.com/feeder",
    "nhr=https://github.com/sknull"
)
@Immutable
data class Opml(
    @XmlElement(false) val version: String? = null,
    @XmlSerialName("head") val head: Head? = null,
    @XmlSerialName("body") val body: Body? = null
)
