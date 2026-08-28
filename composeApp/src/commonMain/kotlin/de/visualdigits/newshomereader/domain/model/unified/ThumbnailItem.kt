package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ThumbnailItem(
    val url: List<String> = listOf(),
    val description: String? = null,
    val author: String? = null,
    val datePublished: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    val width: Int? = null,
    val height: Int? = null
)
