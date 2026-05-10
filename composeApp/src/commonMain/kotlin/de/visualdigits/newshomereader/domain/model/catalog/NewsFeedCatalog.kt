package de.visualdigits.newshomereader.domain.model.catalog

import de.visualdigits.newshomereader.domain.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class NewsFeedCatalog(
    val baseUrl: String,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) val date: OffsetDateTime,
    val categories: List<NewsFeedCatalogCategory>
)
