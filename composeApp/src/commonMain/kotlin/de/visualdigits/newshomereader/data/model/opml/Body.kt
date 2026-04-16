package de.visualdigits.newshomereader.data.model.opml

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
data class Body(
    @XmlSerialName("outline") val outlines: List<Outline> = listOf(),
    @XmlSerialName("settings") val settings: Settings?
) {

    val settingsMap: Map<String, String>?
        get() = settings?.settings?.associate { s -> Pair(s.key, s.value) }
}
