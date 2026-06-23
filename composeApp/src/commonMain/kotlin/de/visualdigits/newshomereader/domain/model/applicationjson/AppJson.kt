package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.newshomereader.domain.serializer.KmpOffsetDateTimeHeuristicDeserializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class AppJson(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("@context") val context: String? = null,
    @SerialName("@graph") val graphs: List<AppJson> = listOf(),
    @SerialName("class") var clazz: String? = null,
    val about: List<About> = listOf(),
    val additionalType: String? = null,
    val alternateName: List<String> = listOf(),
    val alternativeHeadline: String? = null,
    val articleBody: String? = null,
    val articleSection: List<String> = listOf(),
    val author: List<Author> = listOf(),
    val caption: String? = null,
    val commentCount: Int? = null,
    val contentUrl: String? = null,
    val copyrightHolder: CopyrightHolder? = null,
    val copyrightYear: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("dateModified") val dateModified: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("datePublished") val datePublished: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    val description: String? = null,
    val discussionUrl: String? = null,
    val duration: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("expires") val expires: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    val hasPart: List<HasPart> = listOf(),
    val headline: String? = null,
    val identifier: Long? = null,
    val image: List<Image> = listOf(),
    val video: List<Video> = listOf(),
    val inLanguage: String? = null,
    val isAccessibleForFree: Boolean? = null,
    val isFamilyFriendly: Boolean? = null,
    val isPartOf: IsPartOf? = null,
    val itemListElement: List<ItemElement> = listOf(),
    val jobTitle: String? = null,
    val keywords: List<String> = listOf(),
    val logo: Logo? = null,
    val mainEntityOfPage: MainEntityOfPage? = null,
    val name: String? = null,
    @SerialName("potentialAction") val potentialActions: List<PotentialAction> = listOf(),
    @SerialName("primaryImageOfPage") val primaryImageOfPage: List<Image> = listOf(),
    val provider: String? = null,
    val publisher: Publisher? = null,
    val relatedLink: List<String> = listOf(),
    val sourceOrganization: SourceOrganization? = null,
    val thumbnail: List<Image> = listOf(),
    @JsonNames("thumbnailURL", "thumbnailUrl") val thumbnailUrl: List<String> = listOf(),
    val timeRequired: String? = null,
    val transcript: String? = null,
    @Serializable(with = KmpOffsetDateTimeHeuristicDeserializer::class) @XmlSerialName("uploadDate") val uploadDate: KmpOffsetDateTime = KmpOffsetDateTime.now(),
    val url: String? = null,
    val version: String? = null,
    val width: Int? = null,
    val wordCount: Int? = null,
)
