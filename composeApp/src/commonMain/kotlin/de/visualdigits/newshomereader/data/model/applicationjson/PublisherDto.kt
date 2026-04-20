package de.visualdigits.newshomereader.data.model.applicationjson

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.data.serializer.LogoSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Immutable
data class PublisherDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val name: String? = null,
    val url: String? = null,
    @Serializable(with = ListSerializer::class) val alternateName: List<String> = listOf(),
    val correctionsPolicy: String? = null,
    val diversityPolicy: String? = null,
    @Serializable(with = ListSerializer::class) val sameAs: List<String> = listOf(),
    @Serializable(with = LogoSerializer::class) val logo: LogoDto? = null
)
