package de.visualdigits.newshomereader.data.model.opml

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
@Immutable
data class Setting(
    @XmlElement(false) val key: String,
    @XmlElement(false) val value: String
)
