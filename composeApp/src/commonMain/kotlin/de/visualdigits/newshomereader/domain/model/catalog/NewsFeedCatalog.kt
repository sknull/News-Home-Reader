package de.visualdigits.newshomereader.domain.model.catalog

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.Serializable

@Serializable
data class NewsFeedCatalog(
    val baseUrl: String,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) val date: KmpOffsetDateTime,
    val categories: List<NewsFeedCatalogCategory>
)
