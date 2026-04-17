package de.visualdigits.newshomereader.domain.model.unified

import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
@Immutable
data class ThumbnailItem(
    val url: List<String> = listOf(),
    val description: String? = null,
    val author: String? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) val datePublished: OffsetDateTime = OffsetDateTime.now(),
    val width: Int? = null,
    val height: Int? = null
)
