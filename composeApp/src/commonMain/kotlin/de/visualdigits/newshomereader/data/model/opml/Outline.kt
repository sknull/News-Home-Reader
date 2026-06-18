package de.visualdigits.newshomereader.data.model.opml

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.domain.model.opml.OutlineType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
@Immutable
data class Outline(
    @XmlElement(false) val title: String? = null,
    @XmlElement(false) val text: String? = null,
    @XmlElement(false) val xmlUrl: String? = null,
    @XmlElement(false) val type: String? = null,

    @XmlElement(false) @XmlSerialName(prefix = "feeder", namespace="https://nononsenseapps.com/feeder", value = "notify") val notify: String? = null,
    @XmlElement(false) @XmlSerialName(prefix = "feeder", namespace="https://nononsenseapps.com/feeder", value = "imageUrl") val imageUrl: String? = null,
    @XmlElement(false) @XmlSerialName(prefix = "feeder", namespace="https://nononsenseapps.com/feeder", value = "fullTextByDefault") val fullTextByDefault: String? = null,
    @XmlElement(false) @XmlSerialName(prefix = "feeder", namespace="https://nononsenseapps.com/feeder", value = "openArticlesWith") val openArticlesWith: String? = null,
    @XmlElement(false) @XmlSerialName(prefix = "feeder", namespace="https://nononsenseapps.com/feeder", value = "alternateId") val alternateId: String? = null,

    @XmlElement(false) @XmlSerialName(prefix = "nhr", namespace="https://github.com/sknull", value = "outlineType") val outlineType: OutlineType = OutlineType.newsfeed,
    @XmlElement(false) @XmlSerialName(prefix = "nhr", namespace="https://github.com/sknull", value = "stopWords") val stopWords: String? = null,

    @XmlSerialName("outline") val outlines: List<Outline> = listOf()
)
