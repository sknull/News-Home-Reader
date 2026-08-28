package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Item(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("additionalType") val additionalType: String? = null,
    @SerialName("abstract") val abstract: String? = null,
    @SerialName("branding") val branding: String? = null,
    @SerialName("containerId") val containerId: Long? = null,
    @SerialName("headline") val headline: String? = null,
    @SerialName("image") val image: List<Image> = listOf(),
    @SerialName("isAccessibleForFree") val isAccessibleForFree: Boolean? = null,
    @SerialName("isAlert") val isAlert: Boolean? = null,
    @SerialName("isFamilyFriendly") val isFamilyFriendly: Boolean? = null,
    @SerialName("isLive") val isLive: Boolean? = null,
    @SerialName("isUpdate") val isUpdate: Boolean? = null,
    @SerialName("kicker") val kicker: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("sourceOrganization") val sourceOrganization: SourceOrganization? = null,
    @SerialName("url") val url: String? = null
) {

    constructor(url: String? = null): this(id = null, url = url)
}
