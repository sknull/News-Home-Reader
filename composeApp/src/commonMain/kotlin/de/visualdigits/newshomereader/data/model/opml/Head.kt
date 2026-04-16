package de.visualdigits.newshomereader.data.model.opml

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement


@Serializable
data class Head(
    @XmlElement(true) val title: String? = null
)
