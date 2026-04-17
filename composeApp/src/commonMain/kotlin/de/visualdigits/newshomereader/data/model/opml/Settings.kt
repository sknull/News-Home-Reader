package de.visualdigits.newshomereader.data.model.opml

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
@Immutable
data class Settings(
    @XmlSerialName("setting") val settings: List<Setting> = listOf(),
    @XmlSerialName("blocked") val blocked: List<Blocked> = listOf()
)
