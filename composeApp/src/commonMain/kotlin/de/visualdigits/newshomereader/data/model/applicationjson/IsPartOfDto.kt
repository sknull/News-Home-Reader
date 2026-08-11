package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class IsPartOfDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") @Serializable(with = ListSerializer::class) val type: List<String> = listOf(),
    val name: String? = null,
    val productID: String? = null,
)
