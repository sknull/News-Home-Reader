package de.visualdigits.newshomereader.data.webdav

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.newshomereader.domain.model.errorhandling.DataError
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.webdav.SyncState
import de.visualdigits.newshomereader.domain.repository.SettingsRepository
import de.visualdigits.newshomereader.domain.webdav.WebDavSyncService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.time.OffsetDateTime

class DefaultWebDavSyncService(
    private val httpClient: HttpClient,
    private val settingsRepository: SettingsRepository
) : WebDavSyncService {
    private val log = Logger.withTag("WebDavSyncService")

    override suspend fun syncReadStatus(localReadIds: Set<String>): Result<Set<String>, DataError.Remote> {
        return try {
            val settingsResult = settingsRepository.getSettings()
            if (settingsResult is Result.Success) {
                val settings = settingsResult.data?:error("No settings provided")
                val webDavUrl = settings.get<String>(SK.webDavUrl)
                val directory = settings.get<String>(SK.webDavDirectory)?.removePrefix("/")?.removeSuffix("/")
                val url = "$webDavUrl/$directory/newsHomeReader_syncfile.json"
                val remoteState = try {
                    val response = httpClient.get(url)
                    if (response.status.isSuccess()) {
                        response.body<SyncState>()
                    } else {
                        SyncState(OffsetDateTime.now().toInstant().toEpochMilli(), emptySet())
                    }
                } catch (e: Exception) {
                    log.w("Something went wrong while fetching sync file - falling back to empty sync file")
                    SyncState(OffsetDateTime.now().toInstant().toEpochMilli(), emptySet())
                }

                val mergedIds = remoteState.readNewsItemIds + localReadIds

                if (mergedIds.size > remoteState.readNewsItemIds.size || mergedIds.size > localReadIds.size) {
                    val newState = SyncState(
                        lastUpdated = OffsetDateTime.now().toInstant().toEpochMilli(),
                        readNewsItemIds = mergedIds
                    )

                    httpClient.put(url) {
                        setBody(newState)
                        contentType(ContentType.Application.Json)
                    }
                    log.i("Synced read item: ${mergedIds.size}")
                }

                Result.Success(mergedIds)
            } else if (settingsResult is Result.Error) {
                Result.Error(DataError.Remote.UNKNOWN)
            } else {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            log.e("Something went wrong while syncing read items", e)
            Result.Error(DataError.Remote.SERIALIZATION, e)
        }
    }
}
