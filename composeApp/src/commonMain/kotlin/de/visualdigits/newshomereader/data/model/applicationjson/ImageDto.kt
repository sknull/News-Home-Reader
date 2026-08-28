package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class ImageDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val contentUrl: String? = null,
    val caption: String? = null,
    @Serializable(with = ListSerializer::class) val url: List<String> = listOf(),
    val author: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    @XmlSerialName("datePublished") val datePublished: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    val description: String? = null,
    val inLanguage: String? = null,
)
