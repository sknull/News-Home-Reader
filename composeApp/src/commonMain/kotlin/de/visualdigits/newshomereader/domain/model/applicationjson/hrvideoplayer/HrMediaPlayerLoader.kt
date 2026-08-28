package de.visualdigits.newshomereader.domain.model.applicationjson.hrvideoplayer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HrMediaPlayerLoader(
    @SerialName("playerConfig") val playerConfig: PlayerConfig? = null,
    @SerialName("mediaCollection") val mediaCollection: MediaCollection? = null,
    @SerialName("playerId") val playerId: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("typeLabel") val typeLabel: String? = null,
    @SerialName("cssUrl") val cssUrl: String? = null,
    @SerialName("jsUrl") val jsUrl: String? = null,
    @SerialName("isAutoplay") val isAutoplay: Boolean? = null,
    @SerialName("teaserSize") val teaserSize: String? = null
)
