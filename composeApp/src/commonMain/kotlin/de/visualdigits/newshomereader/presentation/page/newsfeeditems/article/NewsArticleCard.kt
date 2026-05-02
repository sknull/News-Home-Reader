package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_timelapse_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemImage
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource
import java.time.format.DateTimeFormatter


@Composable
fun NewsArticleCard(
    modifier: Modifier = Modifier,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .widthIn(max = 1000.dp)
                .padding(MaterialTheme.shapes.gap)
        ) {
            val displayTheme = settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: DisplayThemeEnum.LIGHT

            NewsArticleMenuBar(
                newsItem = newsItem,
                newsArticle = newsArticle,
                uriHandler = uriHandler,
                onAction = onAction
            )

            PlatformVerticalScrollbarBox(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxHeight()
                    .width(10.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
                "newsarticle_${newsArticle.itemId}",
                scrollPosition = scrollPosition,
                onCommonAction
            ) {
                listOf(
                    Pair("feed_name", @Composable {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val feedName = newsItem.newsFeed?.feedName
                            state.lookupNewsFeedMap[feedName?.trim()?.lowercase()]?.url?.let { url ->
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(24.dp)
                                ) {
                                    NewsItemImage(
                                        modifier = Modifier,
                                        url = url.getFaviconUrl(48),
                                        width = 24.dp,
                                        height = 24.dp,
                                        contentDescription = feedName ?: "",
                                        maxImageSize = maxImageSize,
                                        showLoadingIcon = false
                                    )
                                }
                            }

                            newsItem.newsFeed?.feedName?.let { fn ->
                                Text(
                                    modifier = Modifier,
                                    text = fn,
                                    style = if (maxWidth > 600.dp) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }),
                    Pair("title", @Composable {
                        Text(
                            modifier = Modifier,
                            text = newsItem.title,
                            style = if (maxWidth > 600.dp) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium
                        )
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
                                mediaItems = newsArticle.videoItems + newsArticle.audioItems,
                                uriHandler = uriHandler,
                                newsItem = newsItem
                            )
                        }
                    }),
                    Pair("summary", @Composable {
                        if (newsItem.summary.isNotEmpty()) {
                            Text(
                                modifier = Modifier,
                                text = htmlToAnnotatedString(
                                    html = newsItem.summary,
                                    style = HtmlStyle(
                                        textLinkStyles = displayTheme.textLinkStyles
                                    ),
                                    linkInteractionListener = { linkAnnotation ->
                                        makeUrlAbsolute(
                                            newsItem.link,
                                            (linkAnnotation as LinkAnnotation.Url).url
                                        ).let { uriHandler.openUri(it) }
                                    }
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(
//                                fontSize = MaterialTheme.typography.titleMedium.fontSize * 1.5f,
                                    lineHeight = 1.4.em
                                )
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }),
                    Pair("text", @Composable {
                        Text(
                            modifier = Modifier,
                            text = htmlToAnnotatedString(
                                html = newsArticle.html,
                                style = HtmlStyle(
                                    textLinkStyles = displayTheme.textLinkStyles
                                ),
                                linkInteractionListener = { linkAnnotation ->
                                    makeUrlAbsolute(
                                        newsItem.link,
                                        (linkAnnotation as LinkAnnotation.Url).url
                                    ).let { uriHandler.openUri(it) }
                                }
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 1.5.em)
                        )
                    }),
                )
            }
        }
    }
}
