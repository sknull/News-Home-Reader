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
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.util.copyFactor
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.modifier.angledInnerShadow
import de.visualdigits.common.presentation.components.modifier.tintedBackgroundImage
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.circuit_board_squared
import de.visualdigits.compose.resources.icon_paid_24px
import de.visualdigits.compose.resources.icon_timelapse_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.StringEscapeUtils.normalizeXml
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.Image
import de.visualdigits.newshomereader.presentation.style.SPOT_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import de.visualdigits.newshomereader.presentation.style.textLinkStyles
import de.visualdigits.newshomereader.presentation.util.highlightQuery
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import java.time.format.DateTimeFormatter


@Composable
fun NewsArticleCard(
    modifier: Modifier = Modifier,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
    maxWidth: Dp,
    maxImageSize: Int?,
    newsItem: NewsItem,
    newsArticle: FullArticle,
    settings: Settings?,
    uriHandler: UriHandler,
    state: NewsHomeReaderState,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val spotColor = state.settings?.get<HsvColor>(SK.spotColor)?: SPOT_COLOR_DEFAULT
    val backgroundColorValue = HsvColor.fromComposeColor(MaterialTheme.colorScheme.background).value
    val dimFactor = if (backgroundColorValue < 0.5f) 1.5f else 1.25f

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .tintedBackgroundImage(
                    image = imageResource(Res.drawable.circuit_board_squared),
                    tint = MaterialTheme.colorScheme.onSurface,
                    finalAlpha = 0.2f,
                    contentScale = ContentScale.FillHeight
                )
                .angledInnerShadow(
                    angle = 45f,
                    distance = 10.dp,
                    spread = 5.dp,
                    alpha = 0.5f,
                    insetSize = 2.dp,
                    insetColorLight = MaterialTheme.colorScheme.background.copyFactor(valueFactor = dimFactor),
                    insetColorShadow = MaterialTheme.colorScheme.background.copyFactor(valueFactor = 1f / dimFactor)
                )
        ) {}

        Column(
            modifier = modifier
                .fillMaxHeight()
                .widthIn(max = 1000.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(MaterialTheme.shapes.gap)
        ) {
            NewsArticleMenuBar(
                newsItem = newsItem,
                newsArticle = newsArticle,
                uriHandler = uriHandler,
                onAction = onAction
            )

            PlatformVerticalScrollbarBox(
                modifier = Modifier
                    .fillMaxWidth(),
                space = MaterialTheme.shapes.gap,
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .width(10.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                scrollbarStyle = scrollbarStyle(),
                scrollbarId = "newsarticle_${newsArticle.itemId}",
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
                                            uriHandler.openUri(feedUrl?:newsItem.newsFeed.link)
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
                                        uriHandler.openUri(newsItem.link)
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
                        Text(
                            modifier = Modifier,
                            text = "${newsItem.updated.format(DateTimeFormatter.ofPattern("EEE, dd. MMMM yyyy HH:mm"))}",
                            style = MaterialTheme.typography.headlineSmall
                        )
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
                                text = "${newsArticle.readingTime} Min.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }),
                    Pair("image", @Composable {
                        ArticleImage(
                            newsItem = newsItem,
                            newsArticle = newsArticle,
                            maxImageSize = maxImageSize
                        )
                    }),
                    Pair("mediaButtons", @Composable {
                        val wifiOnly = settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
                        if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                            MediaItemButtons(
                                state = state,
                                mediaItems = newsArticle.videoItems + newsArticle.audioItems,
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
                                    ).let { uriHandler.openUri(it) }
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
                            Spacer(Modifier.height(16.dp))
                        }
                    }),
                    Pair("text", @Composable {
                        val annotatedText = htmlToAnnotatedString(
                            html = normalizeXml(newsArticle.html),
                            style = HtmlStyle(
                                textLinkStyles = textLinkStyles(spotColor)
                            ),
                            linkInteractionListener = { linkAnnotation ->
                                makeUrlAbsolute(
                                    newsItem.link,
                                    (linkAnnotation as LinkAnnotation.Url).url
                                ).let { uriHandler.openUri(it) }
                            }
                        )
                        val highlightedText = remember(annotatedText, state.newsItemSearchText) {
                            if (!state.newsItemSearchText.isNullOrBlank()) {
                                annotatedText.highlightQuery(state.newsItemSearchText)
                            } else if (!state.currentKeywordBucket.isNullOrBlank()){
                                annotatedText.highlightQuery(state.currentKeywordBucket)
                            } else {
                                annotatedText
                            }
                        }
                        Text(
                            modifier = Modifier,
                            text = highlightedText,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 1.5.em)
                        )
                    }),
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .tintedBackgroundImage(
                    image = imageResource(Res.drawable.circuit_board_squared),
                    tint = MaterialTheme.colorScheme.onSurface,
                    finalAlpha = 0.2f,
                    contentScale = ContentScale.FillHeight
                )
                .angledInnerShadow(
                    angle = 45f,
                    distance = 10.dp,
                    spread = 5.dp,
                    alpha = 0.5f,
                    insetSize = 2.dp,
                    insetColorLight = MaterialTheme.colorScheme.background.copyFactor(valueFactor = dimFactor),
                    insetColorShadow = MaterialTheme.colorScheme.background.copyFactor(valueFactor = 1f / dimFactor)
                )
        ) {}
    }
}
