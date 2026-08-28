package de.visualdigits.newshomereader.domain.model.applicationjson


import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@Immutable
data class AppJson @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("@id") val id: String? = null,
    @SerialName("@type") val type: String? = null,
    @SerialName("@context") val context: String? = null,
    @SerialName("@graph") val graphs: List<AppJson> = listOf(),
    @SerialName("class") var clazz: String? = null,
    @SerialName("about") val about: List<About> = listOf(),
    @SerialName("additionalType") val additionalType: String? = null,
    @SerialName("alternateName") val alternateName: List<String> = listOf(),
    @SerialName("alternativeHeadline") val alternativeHeadline: String? = null,
    @SerialName("articleBody") val articleBody: String? = null,
    @SerialName("articleSection") val articleSection: List<String> = listOf(),
    @SerialName("author") val author: List<Author> = listOf(),
    @SerialName("caption") val caption: String? = null,
    @SerialName("commentCount") val commentCount: Int? = null,
    @SerialName("contentUrl") val contentUrl: String? = null,
    @SerialName("copyrightHolder") val copyrightHolder: CopyrightHolder? = null,
    @SerialName("copyrightYear") val copyrightYear: String? = null,
    @XmlSerialName("dateModified") val dateModified: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @XmlSerialName("datePublished") val datePublished: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @SerialName("description") val description: String? = null,
    @SerialName("discussionUrl") val discussionUrl: String? = null,
    @SerialName("duration") val duration: String? = null,
    @XmlSerialName("expires") val expires: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @SerialName("hasPart") val hasPart: List<HasPart> = listOf(),
    @SerialName("headline") val headline: String? = null,
    @SerialName("identifier") val identifier: Long? = null,
    @SerialName("image") val image: List<Image> = listOf(),
    @SerialName("video") val video: List<Video> = listOf(),
    @SerialName("inLanguage") val inLanguage: String? = null,
    @SerialName("isAccessibleForFree") val isAccessibleForFree: Boolean? = null,
    @SerialName("isFamilyFriendly") val isFamilyFriendly: Boolean? = null,
    @SerialName("isPartOf") val isPartOf: IsPartOf? = null,
    @SerialName("itemListElement") val itemListElement: List<ItemElement> = listOf(),
    @SerialName("jobTitle") val jobTitle: String? = null,
    @SerialName("keywords") val keywords: List<String> = listOf(),
    @SerialName("logo") val logo: List<Logo> = listOf(),
    @SerialName("mainEntityOfPage") val mainEntityOfPage: MainEntityOfPage? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("potentialAction") val potentialActions: List<PotentialAction> = listOf(),
    @SerialName("primaryImageOfPage") val primaryImageOfPage: List<Image> = listOf(),
    @SerialName("provider") val provider: String? = null,
    @SerialName("publisher") val publisher: Publisher? = null,
    @SerialName("relatedLink") val relatedLink: List<String> = listOf(),
    @SerialName("sourceOrganization") val sourceOrganization: SourceOrganization? = null,
    @SerialName("thumbnail") val thumbnail: List<Image> = listOf(),
    @JsonNames("thumbnailURL", "thumbnailUrl") val thumbnailUrl: List<String> = listOf(),
    @SerialName("timeRequired") val timeRequired: String? = null,
    @SerialName("transcript") val transcript: String? = null,
    @XmlSerialName("uploadDate") val uploadDate: KmpOffsetDateTime = KmpOffsetDateTime.MIN,
    @SerialName("url") val url: String? = null,
    @SerialName("version") val version: String? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("wordCount") val wordCount: Int? = null,
)
