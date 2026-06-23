package de.visualdigits.newshomereader.domain.model.webdav

import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SyncState(
    val lastUpdated: Long? = KmpOffsetDateTime.now().toInstant().toEpochMilliseconds(),
    val readNewsItemIds: Set<String> = setOf()
)
