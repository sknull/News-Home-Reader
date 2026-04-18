package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class BreadcrumbDto(
    @SerialName("@context") val context: String? = null,
    @SerialName("@type") val type: String? = null,
    @Serializable(with = ListSerializer::class) val itemListElement: List<ItemElementDto> = listOf()
)
