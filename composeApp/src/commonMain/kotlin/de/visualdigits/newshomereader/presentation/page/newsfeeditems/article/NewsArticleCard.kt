package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.domain.model.color.HsvColor
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.common.format
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.common.presentation.util.highlightQuery
import de.visualdigits.common.presentation.util.openUriSafely
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_paid_24px
import de.visualdigits.compose.resources.icon_timelapse_24px
import de.visualdigits.essence.model.ImageType
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.StringEscapeUtils.normalizeXml
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.Image
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.SPOT_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.textLinkStyles
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsArticleCard(
    modifier: Modifier = Modifier,
    viewModel: NewsHomeReaderViewModel,
    platformType: PlatformType,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
    maxWidth: Dp,
    maxImageSize: Int?,
    newsItem: NewsItem,
    settings: Settings?,
    uriHandler: UriHandler,
    state: NewsHomeReaderState,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val spotColor = settings?.get<HsvColor>(SK.spotColor)?: SPOT_COLOR_DEFAULT
    val buttonColor = remember { (settings?.get<HsvColor>(SK.buttonColor) ?: BUTTON_COLOR_DEFAULT).toComposeColor() }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .widthIn(max = 1000.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(MaterialTheme.shapes.gap)
        ) {
            NewsArticleMenuBar(
                newsItem = newsItem,
                uriHandler = uriHandler,
                onAction = onAction
            )

            PlatformVerticalScrollbarBox(
                modifier = Modifier
                    .fillMaxWidth(),
                platformType = platformType,
                space = MaterialTheme.shapes.gap,
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .width(10.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                scrollbarId = "newsarticle_${newsItem.newsArticle?.itemId}",
                scrollPosition = scrollPosition,
                onCommonAction = onCommonAction
            ) {
                listOf(
                    Pair("feed_name", @Composable {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val feedName = newsItem.newsFeed?.feedName
                            val feedUrl = state.lookupNewsFeedMap[feedName?.trim()?.lowercase()]?.url
                            feedUrl?.let { url ->
                                Image(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(24.dp),
                                    url = url.getFaviconUrl(48),
                                    width = 24.dp,
                                    height = 24.dp,
                                    contentDescription = feedName ?: "",
                                    maxImageSize = maxImageSize,
                                    showLoadingIcon = false
                                )
                            }

                            newsItem.newsFeed?.feedName?.let { fn ->
                                val interactionSource = remember { MutableInteractionSource() }
                                val isHovered by interactionSource.collectIsHoveredAsState()
                                Text(
                                    modifier = Modifier
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .hoverable(interactionSource)
                                        .clickable {
                                            uriHandler.openUriSafely(feedUrl?:newsItem.newsFeed.link)
                                        },
                                    text = fn,
                                    color = if (isHovered) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                                    style = (if (maxWidth > 600.dp) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall).copy(
                                        textDecoration = if (isHovered) TextDecoration.Underline else TextDecoration.None
                                    )
                                )

                                if (newsItem.newsArticle?.isPaid == true) {
                                    Icon(
                                        modifier = Modifier.size(24.dp)
                                            .width(24.dp)
                                            .height(24.dp),
                                        painter = painterResource(Res.drawable.icon_paid_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }),
                    Pair("title", @Composable {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                            verticalAlignment = Alignment.Top
                        ) {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isHovered by interactionSource.collectIsHoveredAsState()
                            val annotatedTile = htmlToAnnotatedString(normalizeXml(newsItem.title))
                            val highlightedTitle = remember(annotatedTile, state.newsItemSearchText) {
                                if (!state.newsItemSearchText.isNullOrBlank()) {
                                    annotatedTile.highlightQuery(state.newsItemSearchText)
                                } else if (!state.currentKeywordBucket.isNullOrBlank()){
                                    annotatedTile.highlightQuery(state.currentKeywordBucket)
                                } else {
                                    annotatedTile
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .hoverable(interactionSource)
                                    .clickable {
                                        uriHandler.openUriSafely(newsItem.link)
                                    },
                                text = highlightedTitle,
                                color = if (isHovered) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                                style = (if (maxWidth > 600.dp) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium).copy(
                                    textDecoration = if (isHovered) TextDecoration.Underline else TextDecoration.None
                                )
                            )
                        }
                    }),
                    Pair("updated", @Composable {
                        newsItem.updated?.let { u ->
                            if (u > KmpOffsetDateTime.MIN) {
                                Text(
                                    modifier = Modifier,
                                    text = u.toLocalDateTime().format("EEE, dd. MMMM yyyy HH:mm"),
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }),
                    Pair("timeEstimated", @Composable {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            Icon(
                                modifier = Modifier.size(14.dp),
                                painter = painterResource(Res.drawable.icon_timelapse_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${newsItem.newsArticle?.readingTime} Min.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }),
                    Pair("image", @Composable {
                        ArticleImage(
                            newsItem = newsItem,
                            maxImageSize = maxImageSize
                        )
                    }),
                    Pair("mediaButtons", @Composable {
                        val wifiOnly = settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
                        if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                            MediaItemButtons(
                                viewModel = viewModel,
                                mediaItems = (newsItem.newsArticle?.videoItems?:listOf()) + (newsItem.newsArticle?.audioItems?:listOf()) + (newsItem.newsArticle?.imageItems?:listOf()),
                                uriHandler = uriHandler,
                                newsItem = newsItem
                            )
                            Spacer(Modifier.size(MaterialTheme.shapes.gap * 2))
                        }
                    }),
                    Pair("summary", @Composable {
                        if (newsItem.summary.isNotEmpty()) {
                            val annotatedSummary = htmlToAnnotatedString(
                                html = normalizeXml(newsItem.summary),
                                style = HtmlStyle(
                                    textLinkStyles = textLinkStyles(spotColor)
                                ),
                                linkInteractionListener = { linkAnnotation ->
                                    makeUrlAbsolute(
                                        newsItem.link,
                                        (linkAnnotation as LinkAnnotation.Url).url
                                    ).let { uriHandler.openUriSafely(it) }
                                }
                            )
                            val highlightedSummary = remember(annotatedSummary, state.newsItemSearchText) {
                                if (!state.newsItemSearchText.isNullOrBlank()) {
                                    annotatedSummary.highlightQuery(state.newsItemSearchText)
                                } else if (!state.currentKeywordBucket.isNullOrBlank()){
                                    annotatedSummary.highlightQuery(state.currentKeywordBucket)
                                } else {
                                    annotatedSummary
                                }
                            }
                            Text(
                                modifier = Modifier,
                                text = highlightedSummary,
                                style = MaterialTheme.typography.titleMedium.copy(
//                                fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.5f,
                                    lineHeight = 1.4.em
                                )
                            )
                            Spacer(Modifier.height(MaterialTheme.shapes.gap))
                        }
                    }),
                    Pair("text", @Composable {
                        newsItem.newsArticle?.parts?.forEach { part ->
                            when (part.tagName) {
                                "paragraph", "headline" -> {
                                    part.html.forEach { html ->
                                        HighlightedText(html, spotColor, newsItem, uriHandler, state)
                                    }
                                }
                                "div" -> {
                                    if (part.html.isNotEmpty()) {
                                        Spacer(Modifier.height(MaterialTheme.shapes.gap))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(buttonColor, MaterialTheme.shapes.small)
                                                .padding(MaterialTheme.shapes.gap),
                                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                                        ) {
                                            part.html.forEach { html ->
                                                HighlightedText(html, spotColor, newsItem, uriHandler, state)
                                            }
                                        }
                                    }
                                }
                                "img" -> {
                                    if (part.images.isNotEmpty()) {
                                        val icons = part.images
                                            .filter { it.imageType == ImageType.icon.name }
                                        if (icons.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(buttonColor, MaterialTheme.shapes.small)
                                                    .padding(MaterialTheme.shapes.gap),
                                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                                            ) {
                                                icons
                                                    .forEach { icon ->
                                                        Image(
                                                            url = makeUrlAbsolute(
                                                                newsItem.link,
                                                                icon.src
                                                            ),
                                                            contentDescription = icon.alt,
                                                            width = 60.dp,
                                                            height = 60.dp,
                                                            contentScale = ContentScale.Inside,
                                                            maxImageSize = maxImageSize,
                                                            showLoadingIcon = true
                                                        )
                                                    }
                                            }
                                        }

                                        val images = part.images
                                            .filter { it.imageType != ImageType.icon.name }
                                        if (images.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .conditional(maxWidth > 600.dp) { fillMaxWidth(0.6f) }
                                                        .conditional(maxWidth <= 600.dp) { fillMaxWidth() }
                                                        .background(buttonColor, MaterialTheme.shapes.small)
                                                        .padding(MaterialTheme.shapes.gap),
                                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                                                ) {
                                                    images.forEach { img ->
                                                        Column {
                                                            Image(
                                                                url = makeUrlAbsolute(
                                                                    newsItem.link,
                                                                    img.src
                                                                ),
                                                                contentDescription = img.alt,
                                                                maxImageSize = maxImageSize,
                                                                showLoadingIcon = true
                                                            )
                                                            val title = if (img.title?.isNotBlank() == true) {
                                                                img.title
                                                            } else if (img.alt?.isNotBlank() == true) {
                                                                img.alt
                                                            } else null
                                                            title?.let { title ->
                                                                if (title.trim().isNotBlank()) {
                                                                    Text(
                                                                        modifier = Modifier
                                                                            .padding(vertical = MaterialTheme.shapes.gap / 2),
                                                                        text = title.trim(),
                                                                        style = MaterialTheme.typography.bodySmall
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (part.html.isNotEmpty()) {
                                            part.html.forEach { html ->
                                                HighlightedText(html, spotColor, newsItem, uriHandler, state)
                                            }
                                        }
                                    }
                                }
                                else -> {}
                            }
                        }
                    }),
                )
            }
        }
    }
}

@Composable
private fun HighlightedText(
    html: String,
    spotColor: HsvColor,
    newsItem: NewsItem,
    uriHandler: UriHandler,
    state: NewsHomeReaderState
) {
    val annotatedText = htmlToAnnotatedString(
        html = normalizeXml(html),
        style = HtmlStyle(
            textLinkStyles = textLinkStyles(spotColor)
        ),
        linkInteractionListener = { linkAnnotation ->
            makeUrlAbsolute(
                newsItem.link,
                (linkAnnotation as LinkAnnotation.Url).url
            ).let { uriHandler.openUriSafely(it) }
        }
    )
    val highlightedText = remember(annotatedText, state.newsItemSearchText) {
        if (!state.newsItemSearchText.isNullOrBlank()) {
            annotatedText.highlightQuery(state.newsItemSearchText)
        } else if (!state.currentKeywordBucket.isNullOrBlank()) {
            annotatedText.highlightQuery(state.currentKeywordBucket)
        } else {
            annotatedText
        }
    }
    val lineHeight = if (html.startsWith("<h")) 2.0.em else 1.5.em
    Text(
        modifier = Modifier
            .padding(vertical = MaterialTheme.shapes.gap),
        text = highlightedText,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = lineHeight)
    )
}
