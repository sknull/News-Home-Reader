package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.newshomereader.data.model.applicationjson.AboutDto
import de.visualdigits.newshomereader.data.model.applicationjson.AppJsonDto
import de.visualdigits.newshomereader.data.model.applicationjson.AuthorDto
import de.visualdigits.newshomereader.data.model.applicationjson.BreadcrumbDto
import de.visualdigits.newshomereader.data.model.applicationjson.CopyrightHolderDto
import de.visualdigits.newshomereader.data.model.applicationjson.HasPartDto
import de.visualdigits.newshomereader.data.model.applicationjson.ImageDto
import de.visualdigits.newshomereader.data.model.applicationjson.IsPartOfDto
import de.visualdigits.newshomereader.data.model.applicationjson.ItemDto
import de.visualdigits.newshomereader.data.model.applicationjson.ItemElementDto
import de.visualdigits.newshomereader.data.model.applicationjson.LogoDto
import de.visualdigits.newshomereader.data.model.applicationjson.MainEntityOfPageDto
import de.visualdigits.newshomereader.data.model.applicationjson.PotentialActionDto
import de.visualdigits.newshomereader.data.model.applicationjson.PublisherDto
import de.visualdigits.newshomereader.data.model.applicationjson.QueryInputDto
import de.visualdigits.newshomereader.data.model.applicationjson.SourceOrganizationDto
import de.visualdigits.newshomereader.data.model.applicationjson.TargetDto
import de.visualdigits.newshomereader.data.model.applicationjson.VideoDto
import de.visualdigits.newshomereader.domain.model.applicationjson.About
import de.visualdigits.newshomereader.domain.model.applicationjson.AppJson
import de.visualdigits.newshomereader.domain.model.applicationjson.Author
import de.visualdigits.newshomereader.domain.model.applicationjson.Breadcrumb
import de.visualdigits.newshomereader.domain.model.applicationjson.CopyrightHolder
import de.visualdigits.newshomereader.domain.model.applicationjson.HasPart
import de.visualdigits.newshomereader.domain.model.applicationjson.Image
import de.visualdigits.newshomereader.domain.model.applicationjson.IsPartOf
import de.visualdigits.newshomereader.domain.model.applicationjson.Item
import de.visualdigits.newshomereader.domain.model.applicationjson.ItemElement
import de.visualdigits.newshomereader.domain.model.applicationjson.Logo
import de.visualdigits.newshomereader.domain.model.applicationjson.MainEntityOfPage
import de.visualdigits.newshomereader.domain.model.applicationjson.PotentialAction
import de.visualdigits.newshomereader.domain.model.applicationjson.Publisher
import de.visualdigits.newshomereader.domain.model.applicationjson.QueryInput
import de.visualdigits.newshomereader.domain.model.applicationjson.SourceOrganization
import de.visualdigits.newshomereader.domain.model.applicationjson.Target
import de.visualdigits.newshomereader.domain.model.applicationjson.Video


fun AboutDto.toAbout(): About {
    return About(
        id = id,
        type = type,
        name = name,
        sameAs = sameAs
    )
}

fun AuthorDto.toAuthor(): Author {
    return Author(
        id = id,
        type = type,
        name = name,
        url = url
    )
}

fun CopyrightHolderDto.toCopyrightHolder(): CopyrightHolder {
    return CopyrightHolder(
        id = id,
        type = type,
        name = name
    )
}

fun HasPartDto.toHasPart(): HasPart {
    return HasPart(
        id = id,
        type = type,
        name = name,
        startOffset = startOffset,
        endOffset = endOffset,
        url = url
    )
}

fun ImageDto.toImage(): Image {
    return Image(
        id = id,
        type = type,
        name = name,
        contentUrl = contentUrl,
        caption = caption,
        url = url,
        author = author,
        width = width,
        height = height,
        datePublished = datePublished,
        description = description,
        inLanguage = inLanguage
    )
}

fun VideoDto.toVideo(): Video {
    return Video(
        context = context,
        type = type,
        name = name,
        description = description,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        contentUrl = contentUrl,
        uploadDate = uploadDate,
        publisher = publisher
    )
}

fun IsPartOfDto.toIsPartOf(): IsPartOf {
    return IsPartOf(
        id = id,
        type = type,
        name = name,
        productID = productID
    )
}

fun ItemDto.toItemElement(): Item {
    return Item(
        url = url
    )
}

fun ItemElementDto.toItemElement(): ItemElement {
    return ItemElement(
        id = id,
        type = type,
        containerId = containerId,
        position = position,
        item = item?.toItemElement()
    )
}

fun LogoDto.toLogo(): Logo {
    return Logo(
        id = id,
        type = type,
        url = url,
        caption = caption,
        contentUrl = contentUrl,
        inLanguage = inLanguage,
        width = width,
        height = height
    )
}

fun BreadcrumbDto.toBreadcrumb(): Breadcrumb {
    return Breadcrumb(
        context = context,
        type = type,
        itemListElement = itemListElement.map { i -> i.toItemElement() }
    )
}

fun MainEntityOfPageDto.toMainEntityOfPage(): MainEntityOfPage {
    return MainEntityOfPage(
        id = id,
        type = type,
        url = url,
        breadcrumb = breadcrumb?.toBreadcrumb()
    )
}

fun TargetDto.toTarget(): Target {
    return Target(
        urlTemplate = urlTemplate
    )
}

fun QueryInputDto.toQueryInput(): QueryInput {
    return QueryInput(
        id = id,
        type = type,
        valueRequired = valueRequired,
        valueName = valueName,
        inLanguage = inLanguage,
        url = url,
        contentUrl = contentUrl,
        width = width,
        height = height,
        caption = caption
    )
}

fun PotentialActionDto.toPotentialAction(): PotentialAction {
    return PotentialAction(
        id = id,
        type = type,
        name = name,
        target = target?.targets?.map { t -> t.toTarget()  }?:listOf(),
        startOffsetInput = startOffsetInput,
        queryInput = queryInput?.toQueryInput()
    )
}

fun PublisherDto.toPublisher(): Publisher {
    return Publisher(
        id = id,
        type = type,
        name = name,
        url = url,
        alternateName = alternateName,
        correctionsPolicy = correctionsPolicy,
        diversityPolicy = diversityPolicy,
        sameAs = sameAs,
        logo = logo?.toLogo()
    )
}

fun SourceOrganizationDto.toSourceOrganization(): SourceOrganization {
    return SourceOrganization(
        id = id,
        type = type,
        identifier = identifier,
        name = name
    )
}

fun AppJsonDto.toAppJson(): AppJson {
    return AppJson(
        id = id,
        type = type,
        context = context,
        graphs = graphs.map { g -> g.toAppJson() },
        clazz = clazz,
        about = about.map { a -> a.toAbout() },
        additionalType = additionalType,
        alternateName = alternateName,
        alternativeHeadline = alternativeHeadline,
        articleBody = articleBody,
        articleSection = articleSection,
        author = author?.autors?.map { a -> a.toAuthor() }?:listOf(),
        caption = caption,
        commentCount = commentCount,
        contentUrl = contentUrl,
        copyrightHolder = copyrightHolder?.toCopyrightHolder(),
        copyrightYear = copyrightYear,
        dateModified = dateModified,
        datePublished = datePublished,
        description = description,
        discussionUrl = discussionUrl,
        duration = duration,
        expires = expires,
        hasPart = hasPart.map { h -> h.toHasPart() },
        headline = headline,
        identifier = identifier,
        image = image?.images?.map { i -> i.toImage() }?:listOf(),
        video = video?.videos?.map { i -> i.toVideo() }?:listOf(),
        inLanguage = inLanguage,
        isAccessibleForFree = isAccessibleForFree,
        isFamilyFriendly = isFamilyFriendly,
        isPartOf = isPartOf?.toIsPartOf(),
        itemListElement = itemListElement.map { i -> i.toItemElement() },
        jobTitle = jobTitle,
        keywords = keywords,
        logo = logo?.toLogo(),
        mainEntityOfPage = mainEntityOfPage?.toMainEntityOfPage(),
        name = name,
        potentialActions = potentialActions.map { a -> a.toPotentialAction() },
        primaryImageOfPage = primaryImageOfPage?.images?.map { i -> i.toImage() }?:listOf(),
        provider = provider,
        publisher = publisher?.toPublisher(),
        relatedLink = relatedLink,
        sourceOrganization = sourceOrganization?.toSourceOrganization(),
        thumbnail = thumbnail?.images?.map { i -> i.toImage() }?:listOf(),
        thumbnailUrl = thumbnailUrl,
        timeRequired = timeRequired,
        transcript = transcript,
        uploadDate = uploadDate,
        url = url,
        version = version,
        width = width,
        wordCount = wordCount
    )
}
