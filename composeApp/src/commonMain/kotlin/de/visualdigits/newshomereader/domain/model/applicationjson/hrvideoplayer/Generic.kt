package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Generic(
    @SerialName("isAutoplay") val isAutoplay: Boolean? = null,
    @SerialName("imageTemplateConfig") val imageTemplateConfig: ImageTemplateConfig? = null
)
