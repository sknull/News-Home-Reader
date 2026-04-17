package de.visualdigits.newshomereader.data.model.opml

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
@Immutable
data class Blocked(
    @XmlElement(false) val pattern: String = ""
)
