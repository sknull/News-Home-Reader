package de.visualdigits.newshomereader.data.model.rss

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@Immutable
data class Guid(
    @XmlElement(false) val isPermaLink: Boolean? = null,
    @XmlValue val text: String
)
