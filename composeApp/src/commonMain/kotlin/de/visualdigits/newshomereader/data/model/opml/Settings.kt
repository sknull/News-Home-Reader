package de.visualdigits.newshomereader.data.model.opml

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
data class Settings(
    @XmlSerialName("setting") val settings: List<Setting> = listOf(),
    @XmlSerialName("blocked") val blocked: List<Blocked> = listOf()
)
