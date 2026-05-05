package de.visualdigits.newshomereader.domain.model.webdav

import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class SyncState(
    val lastUpdated: Long? = OffsetDateTime.now().toInstant().toEpochMilli(),
    val readNewsItemIds: Set<String> = setOf()
)
