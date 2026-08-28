package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("durationSeconds") val durationSeconds: Int? = null,
    @SerialName("images") val images: List<Image> = listOf(),
    @SerialName("title") val title: String? = null
)
