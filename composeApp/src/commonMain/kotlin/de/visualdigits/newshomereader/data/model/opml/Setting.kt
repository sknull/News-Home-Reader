package de.visualdigits.newshomereader.data.model.opml

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
data class Setting(
    @XmlElement(false) val key: String,
    @XmlElement(false) val value: String
)
