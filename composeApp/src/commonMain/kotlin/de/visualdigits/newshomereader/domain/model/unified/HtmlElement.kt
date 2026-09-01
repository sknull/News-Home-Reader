package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class HtmlElement(
    val tagName: String,
    val html: List<String> = listOf(),
    val imageType: String? = null,
    val src: String? = null,
)
