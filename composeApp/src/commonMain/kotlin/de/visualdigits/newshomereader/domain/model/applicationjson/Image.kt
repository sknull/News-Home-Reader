package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
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
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("datePublished") val datePublished: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    val description: String? = null,
    val inLanguage: String? = null,
)
