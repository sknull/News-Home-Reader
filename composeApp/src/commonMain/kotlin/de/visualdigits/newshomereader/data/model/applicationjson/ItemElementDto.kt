package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ItemSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ItemElementDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val containerId: String? = null,
    val position: Int? = null,
    @Serializable(with = ItemSerializer::class) val item: ItemDto? = null
)
