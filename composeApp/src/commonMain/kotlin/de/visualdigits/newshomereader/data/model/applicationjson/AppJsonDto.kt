package de.visualdigits.newshomereader.data.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.newshomereader.data.serializer.ListSerializer
import de.visualdigits.newshomereader.data.serializer.MainEntityOfPageSerializer
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.ThumbnailItem
import de.visualdigits.newshomereader.domain.serializer.OffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.io.File
import java.time.OffsetDateTime

@Serializable
@Immutable
data class AppJsonDto @OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class) constructor(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("@context") val context: String? = null,
    @SerialName("@graph") val graphs: List<AppJsonDto> = listOf(),
    @SerialName("class") var clazz: String? = null,
    @Serializable(with = ListSerializer::class) val about: List<AboutDto> = listOf(),
    val additionalType: String? = null,
    @Serializable(with = ListSerializer::class) val alternateName: List<String> = listOf(),
    val alternativeHeadline: String? = null,
    val articleBody: String? = null,
    val articleSection: String? = null,
    val author: AuthorWrapper? = null,
    val caption: String? = null,
    val commentCount: Int? = null,
    val contentUrl: String? = null,
    val copyrightHolder: CopyrightHolderDto? = null,
    val copyrightYear: String? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("dateModified") val dateModified: OffsetDateTime = OffsetDateTime.now(),
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("datePublished") val datePublished: OffsetDateTime = OffsetDateTime.now(),
    val description: String? = null,
    val discussionUrl: String? = null,
    val duration: String? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("expires") val expires: OffsetDateTime = OffsetDateTime.now(),
    @Serializable(with = ListSerializer::class) val hasPart: List<HasPartDto> = listOf(),
    val headline: String? = null,
    val identifier: Long? = null,
    val image: ImageWrapper? = null,
    val video: VideoWrapper? = null,
    val inLanguage: String? = null,
    val isAccessibleForFree: Boolean? = null,
    val isFamilyFriendly: Boolean? = null,
    val isPartOf: IsPartOfDto? = null,
    @Serializable(with = ListSerializer::class) val itemListElement: List<ItemElementDto> = listOf(),
    val jobTitle: String? = null,
    @Serializable(with = ListSerializer::class) val keywords: List<String> = listOf(),
    val logo: LogoDto? = null,
    @Serializable(with = MainEntityOfPageSerializer::class) val mainEntityOfPage: MainEntityOfPageDto? = null,
    val name: String? = null,
    @Serializable(with = ListSerializer::class) @SerialName("potentialAction") val potentialActions: List<PotentialActionDto> = listOf(),
    @SerialName("primaryImageOfPage") val primaryImageOfPage: ImageWrapper? = null,
    val provider: String? = null,
    val publisher: PublisherDto? = null,
    @Serializable(with = ListSerializer::class) val relatedLink: List<String> = listOf(),
    val sourceOrganization: SourceOrganizationDto? = null,
    val thumbnail: ImageWrapper? = null,
    @JsonNames("thumbnailURL", "thumbnailUrl") @Serializable(with = ListSerializer::class) val thumbnailUrl: List<String> = listOf(),
    val timeRequired: String? = null,
    val transcript: String? = null,
    @Serializable(with = OffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("uploadDate") val uploadDate: OffsetDateTime = OffsetDateTime.now(),
    val url: String? = null,
    val version: String? = null,
    val width: Int? = null,
    val wordCount: Int? = null,
) {

    companion object {

        val mapper = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            explicitNulls = false
            encodeDefaults = false
        }

        fun decodeFromString(json: String): AppJsonDto {
            return mapper.decodeFromString(json)
        }

        fun decodeValue(file: File): AppJsonDto {
            return mapper.decodeFromString(file.readText())
        }
    }

    fun toMediaItem(): MediaItem {
        return MediaItem(
            url = contentUrl?:url,
            headline = headline,
            description = description,
            datePublished = datePublished,
            dateModified = dateModified,
            uploadDate = uploadDate,
            expires = expires,
            keywords = keywords,
            thumbnails = ((image?.images?:listOf()) + (thumbnail?.images?:listOf()) + (primaryImageOfPage?.images?:listOf())).map { io ->
                ThumbnailItem(
                    url = io.contentUrl?.let { cu -> listOf(cu) }?:io.url,
                    description = io.description,
                    author = io.author,
                    datePublished = io.datePublished,
                    width = io.width,
                    height = io.height
                )
            }?:listOf()
        )
    }
}
