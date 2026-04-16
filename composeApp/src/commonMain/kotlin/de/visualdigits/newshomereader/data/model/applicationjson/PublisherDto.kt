package de.visualdigits.newshomereader.data.model.applicationjson

import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PublisherDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val url: String? = null,
    @Serializable(with = ListSerializer::class) val alternateName: List<String> = listOf(),
    val correctionsPolicy: String? = null,
    val diversityPolicy: String? = null,
    @Serializable(with = ListSerializer::class) val sameAs: List<String> = listOf(),
    val logo: LogoDto? = null
)
