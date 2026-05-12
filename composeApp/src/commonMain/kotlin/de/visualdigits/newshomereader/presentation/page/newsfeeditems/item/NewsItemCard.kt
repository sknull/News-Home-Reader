package de.visualdigits.newshomereader.presentation.page.newsfeeditems.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_paid_24px
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.compose.resources.icon_volume_up_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.StringEscapeUtils.normalizeXml
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.textLinkStyles
import de.visualdigits.newshomereader.presentation.util.highlightQuery
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource
import java.time.format.DateTimeFormatter


@Composable
fun NewsItemCard(
    modifier: Modifier = Modifier,
    state: NewsHomeReaderState,
    simple: Boolean = false,
    maxImageSize: Int?,
    newsItem: NewsItem,
    settings: Settings?,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val displayTheme = settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: DisplayThemeEnum.LIGHT
    val feedName = newsItem.newsFeed?.feedName?:newsItem.feedName
    val spotColor = state.settings?.get<Color>(SK.spotColor)?: DisplayThemeEnum.SPOT_COLOR_DEFAULT


    Box(
        modifier = modifier
            .conditional(simple) { height(100.dp) }
            .conditional(!simple) { dropShadow(
                shape = RoundedCornerShape(20.dp),
                shadow = Shadow(
                    radius = 6.dp,
                    spread = 2.dp,
                    color = Color.Black.copy(alpha = 0.2f),
                    offset = DpOffset(2.dp, 2.dp)
                )
            )}
    ) {
        Column(
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.small)
                .hoverable(interactionSource)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable {
                    onAction(
                        NewsHomeReaderAction.OnNewsItemClicked(
                            newsItem = newsItem
                        )
                    )
                }
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                    }
                }
        ) {
            // teaser image (for non simple layout)
            if (!simple) {
                var image = newsItem.image
                if (image.isEmpty()) {
                    image = newsItem.newsArticle?.articleImage?:""
                }
                if (image.isNotEmpty()) {
                    Image(
                        url = image,
                        contentDescription = newsItem.imageCaption,
                        maxImageSize = maxImageSize
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
            ) {
                // teaser image (for simple layout)
                if (simple) {
                    var image = newsItem.image
                    if (image.isEmpty()) {
                        image = newsItem.newsArticle?.articleImage ?: ""
                    }
                    if (image.isNotEmpty()) {
                        Image(
                            modifier = Modifier
                                .weight(0.5f)
                                .fillMaxHeight(),
                            url = image,
                            contentScale = ContentScale.FillHeight,
                            contentDescription = newsItem.imageCaption,
                            maxImageSize = 640
                        )
                    }
                }

                // inner column to provide padding for all text content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(MaterialTheme.shapes.gap),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {
                    // favicon and date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                    ) {
                        if (state.currentNewsFeedGroup != null || simple) {
                            state.lookupNewsFeedMap[feedName.trim().lowercase()]?.url?.let { url ->
                                // favicon
                                Image(
                                    modifier = Modifier
                                        .width(30.dp),
                                    url = url.getFaviconUrl(48),
                                    width = 30.dp,
                                    contentDescription = feedName,
                                    maxImageSize = 48,
                                    showLoadingIcon = false
                                )
                            }
                        }

                        // updated date
                        Text(
                            text = "${newsItem.updated.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        // indicators
                        if (newsItem.newsArticle?.videoItems?.isNotEmpty() == true) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_videocam_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (newsItem.newsArticle?.audioItems?.isNotEmpty() == true) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_volume_up_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (newsItem.newsArticle?.isFree == false) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_paid_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    val annotatedTitle = htmlToAnnotatedString(
                        html = normalizeXml(newsItem.title),
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
                    val highlightedTitle = remember(annotatedTitle, state.newsItemSearchText) {
                        if (!state.newsItemSearchText.isNullOrBlank()) {
                            annotatedTitle.highlightQuery(state.newsItemSearchText)
                        } else {
                            annotatedTitle
                        }
                    }
                    Text(
                        text = highlightedTitle,
                        style = MaterialTheme.typography.titleSmall
                    )

                    if (!simple) {
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
                            } else {
                                annotatedSummary
                            }
                        }
                        Text(
                            modifier = Modifier,
                            text = highlightedSummary,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 1.2.em)
                        )
                    }
                }
            }
        }
    }
}
