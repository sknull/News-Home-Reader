package de.visualdigits.newshomereader.domain.model.catalog

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NewsFeedCatalog(
    val baseUrl: String,
    val date: KmpOffsetDateTime,
    val categories: List<NewsFeedCatalogCategory>
)
