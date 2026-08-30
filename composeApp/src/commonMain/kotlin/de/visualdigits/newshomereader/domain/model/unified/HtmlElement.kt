package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class HtmlElement(
    val tagName: String,
    val html: String? = null,
    val href: String? = null,
    val alt: String? = null
)
