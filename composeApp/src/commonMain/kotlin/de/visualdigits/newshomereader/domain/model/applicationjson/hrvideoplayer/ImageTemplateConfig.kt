package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageTemplateConfig(
    @SerialName("size") val size: List<Size> = listOf()
)
