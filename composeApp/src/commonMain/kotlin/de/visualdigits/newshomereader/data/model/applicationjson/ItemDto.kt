package de.visualdigits.newshomereader.data.model.applicationjson


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    val additionalType: String? = null,
    val abstract: String? = null,
    val branding: String? = null,
    val containerId: Long? = null,
    val headline: String? = null,
    @SerialName("image") val image: ImageWrapper? = null,
    val isAccessibleForFree: Boolean? = null,
    val isAlert: Boolean? = null,
    val isFamilyFriendly: Boolean? = null,
    val isLive: Boolean? = null,
    val isUpdate: Boolean? = null,
    val kicker: String? = null,
    val name: String? = null,
    val sourceOrganization: SourceOrganizationDto? = null,
    val url: String? = null
) {

    constructor(url: String? = null): this(id = null, url = url)
}
