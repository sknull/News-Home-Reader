package de.visualdigits.newshomereader.data.model.rdf

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@Immutable
data class Content(
    @XmlValue val text: String
)
