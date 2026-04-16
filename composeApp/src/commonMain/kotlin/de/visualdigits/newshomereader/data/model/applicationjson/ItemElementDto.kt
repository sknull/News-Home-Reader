package de.visualdigits.newshomereader.data.model.applicationjson


import de.visualdigits.newshomereader.data.serializer.ItemSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemElementDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val containerId: String? = null,
    val position: Int? = null,
    @Serializable(with = ItemSerializer::class) val item: ItemDto? = null
)
