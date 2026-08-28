package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class Image(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("contentUrl") val contentUrl: String? = null,
    @SerialName("caption") val caption: String? = null,
    @SerialName("url") val url: List<String> = listOf(),
    @SerialName("author") val author: String? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null,
    @XmlSerialName("datePublished") val datePublished: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @SerialName("description") val description: String? = null,
    @SerialName("inLanguage") val inLanguage: String? = null,
)
