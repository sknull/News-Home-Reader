package de.visualdigits.newshomereader.domain.model.applicationjson


import de.visualdigits.newshomereader.data.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.time.OffsetDateTime

@Serializable
data class Image(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val contentUrl: String? = null,
    val caption: String? = null,
    val url: List<String> = listOf(),
    val author: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("datePublished") val datePublished: OffsetDateTime = OffsetDateTime.now(),
    val description: String? = null,
    val inLanguage: String? = null,
)
