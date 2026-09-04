package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ImageElement(
    val src: String,
    val alt: String? = null,
    val title: String? = null,
    val imageType: String
)
