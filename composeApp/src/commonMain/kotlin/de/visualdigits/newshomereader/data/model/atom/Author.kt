package de.visualdigits.newshomereader.data.model.atom

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
@Immutable
data class Author(
    @XmlElement(true) val name: String? = null,
    @XmlElement(true) val uri: String? = null
)
