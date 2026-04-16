package de.visualdigits.translation.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue


@Serializable
data class ResourceString(
    @XmlSerialName("name") val name: String,
    @XmlValue val value: String
)
