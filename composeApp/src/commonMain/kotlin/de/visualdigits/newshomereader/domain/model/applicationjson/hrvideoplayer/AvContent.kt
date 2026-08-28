package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvContent(
    @SerialName("av_content_id") val avContentId: String? = null,
    @SerialName("av_content") val avContent: String? = null,
    @SerialName("av_content_type") val avContentType: String? = null,
    @SerialName("av_content_duration") val avContentDuration: Int? = null,
    @SerialName("av_broadcasting_type") val avBroadcastingType: String? = null,
    @SerialName("av_content_theme1") val avContentTheme1: String? = null,
    @SerialName("av_content_theme2") val avContentTheme2: String? = null,
    @SerialName("av_content_theme3") val avContentTheme3: String? = null,
    @SerialName("hr_document_type") val hrDocumentType: String? = null,
    @SerialName("site_level2_id") val siteLevel2Id: String? = null
)
