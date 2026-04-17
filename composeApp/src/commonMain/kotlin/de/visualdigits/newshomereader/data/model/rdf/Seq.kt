package de.visualdigits.newshomereader.data.model.rdf

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class Seq(
    @XmlElement(true) @XmlSerialName("li") val li: Li
)
