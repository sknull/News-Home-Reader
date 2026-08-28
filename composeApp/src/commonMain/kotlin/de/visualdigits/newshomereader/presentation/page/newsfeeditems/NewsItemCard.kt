package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.domain.model.color.HsvColor
import de.visualdigits.common.domain.model.common.format
import de.visualdigits.common.presentation.util.highlightQuery
import de.visualdigits.common.presentation.util.openUriSafely
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_paid_24px
import de.visualdigits.compose.resources.icon_photo_24px
import de.visualdigits.compose.resources.icon_playlist_add_check_24px
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.compose.resources.icon_volume_up_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.StringEscapeUtils.normalizeXml
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.SPOT_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.textLinkStyles
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource


@Composable
fun NewsItemCard(
    modifier: Modifier = Modifier,
    viewModel: NewsHomeReaderViewModel,
    state: NewsHomeReaderState,
    maxImageSize: Int?,
    newsItem: NewsItem,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }
    val buttonColor = remember { (settings?.get<HsvColor>(SK.buttonColor) ?: BUTTON_COLOR_DEFAULT).toComposeColor() }

    val feedName = newsItem.newsFeed?.feedName?:newsItem.feedName
    val spotColor = settings?.get<HsvColor>(SK.spotColor)?: SPOT_COLOR_DEFAULT

    Box(
        modifier = modifier
            .dropShadow(
                shape = RoundedCornerShape(8.dp),
                shadow = Shadow(
                    radius = 4.dp,
                    spread = 2.dp,
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = DpOffset((-5).dp, 5.dp)
                )
            )
    ) {
        Column(
            modifier = modifier
                .clip(MaterialTheme.shapes.small)
                .background(buttonColor, MaterialTheme.shapes.small)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
            ) {
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
                        if (state.currentNewsFeedGroup != null || state.currentKeywordBucket != null) {
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
                        newsItem.updated?.toLocalDateTime()?.let { u ->
                            val text = u.format("dd.MM.yyyy HH:mm")
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        //
                        // indicators
                        //
                        val hasFullArticle = newsItem.newsArticle != null
                        val hasVideoItems = newsItem.newsArticle?.videoItems?.isNotEmpty() == true
                        val hasAudioItems = newsItem.newsArticle?.audioItems?.isNotEmpty() == true
                        val hasImageItems = newsItem.newsArticle?.imageItems?.isNotEmpty() == true

                        // only show has article indicator when there is no other indication
                        if (!hasVideoItems && !hasAudioItems && !hasImageItems && hasFullArticle) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_playlist_add_check_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (hasVideoItems) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_videocam_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (hasAudioItems) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_volume_up_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (hasImageItems) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.icon_photo_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (newsItem.newsArticle?.isPaid == true) {
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
                            ).let { uriHandler.openUriSafely(it) }
                        }
                    )
                    val highlightedTitle = remember(annotatedTitle, state.newsItemSearchText) {
                        if (!state.newsItemSearchText.isNullOrBlank()) {
                            annotatedTitle.highlightQuery(state.newsItemSearchText)
                        } else if (!state.currentKeywordBucket.isNullOrBlank()){
                            annotatedTitle.highlightQuery(state.currentKeywordBucket)
                        } else {
                            annotatedTitle
                        }
                    }
                    Text(
                        text = highlightedTitle,
                        style = MaterialTheme.typography.titleSmall
                    )

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
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 1.2.em)
                    )
                }
            }
        }
    }
}
