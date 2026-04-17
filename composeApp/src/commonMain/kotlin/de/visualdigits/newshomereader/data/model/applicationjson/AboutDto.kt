package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AboutDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @Serializable(with = ListSerializer::class) val name: List<String> = listOf(),
    @Serializable(with = ListSerializer::class) val sameAs: List<String> = listOf()
)
