package de.visualdigits.newshomereader.data.model.atom

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
@Immutable
data class Link(
    @XmlElement(false) val rel: String? = null,
    @XmlElement(false) val type: String? = null,
    @XmlElement(false) val href: String? = null
)
