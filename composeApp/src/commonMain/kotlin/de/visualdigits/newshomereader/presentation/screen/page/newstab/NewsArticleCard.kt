package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbar
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_arrow_back_24px
import de.visualdigits.compose.resources.icon_chat_24px
import de.visualdigits.compose.resources.icon_emergency_home_24px
import de.visualdigits.compose.resources.icon_link_24px
import de.visualdigits.compose.resources.icon_speaker_2_24px
import de.visualdigits.compose.resources.icon_timelapse_24px
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.compose.resources.tooltip_back
import de.visualdigits.compose.resources.tooltip_open_chat
import de.visualdigits.compose.resources.tooltip_open_link
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.MediaType
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.net.URI
import java.time.format.DateTimeFormatter


@Composable
fun NewsArticleCard(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    maxHeight: Dp,
    uriHandler: UriHandler,
    newsItem: NewsItem,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val newsArticle = state.currentNewsArticle

    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState(viewModel.scrollPosition["newsarticle_${newsArticle?.itemId}"]?:0)
    LaunchedEffect(scrollState.value) {
        onAction(NewsHomeReaderAction.OnScrollPositionChange("newsarticle_${newsArticle?.itemId}", scrollState.value))
    }

    // scrollbar box
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val displayTheme = state.settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: DisplayThemeEnum.LIGHT

        NewsArticleMenuBar(
            uriHandler = uriHandler,
            currentNewsItem = newsItem,
            currentNewsArticle = newsArticle,
            onAction = onAction
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(MaterialTheme.shapes.gap)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {

                    Text(
                        modifier = Modifier,
                        text = newsItem.title,
                        style = if (maxWidth > 600.dp) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        modifier = Modifier,
                        text = "${newsItem.updated.format(DateTimeFormatter.ofPattern("EEE, dd. MMMM yyyy HH:mm"))}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_timelapse_24px),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${newsArticle?.readingTime} Min.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    ArticleImage(modifier, newsItem, newsArticle, maxImageSize = state.maxImageSize)

                    val wifiOnly = state.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
                    if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                        MediaItemButtons(
                            modifier = modifier,
                            mediaItems = (newsArticle?.videoItems?:listOf()) + (newsArticle?.audioItems?:listOf()),
                            uriHandler = uriHandler,
                            currentNewsItem = newsItem
                        )
                    }

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
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier.height(16.dp))
                    }

                    Text(
                        modifier = Modifier,
                        text = htmlToAnnotatedString(
                            html = newsArticle?.html?:"",
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
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            PlatformVerticalScrollbar(
                interactionSource = interactionSource,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
                    .width(8.dp),
                scrollState = scrollState
            )
        }
    }
}

@Composable
private fun ArticleImage(
    modifier: Modifier,
    newsItem: NewsItem,
    newsArticle: FullArticle?,
    maxImageSize: Int?
) {
    if (newsArticle?.articleImage?.isNotEmpty() == true) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
        ) {
            if (newsItem.imageTitle.isNotEmpty()) {
                Text(
                    text = newsItem.imageTitle,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            val builder = ImageRequest
                .Builder(LocalPlatformContext.current)
                .data(newsArticle.articleImage)
                .listener(
                    onStart = {},
                    onSuccess = { _, _ -> },
                    onCancel = {},
                    onError = { _, result -> Logger.e("Image load failed: $newsArticle.articleImage", result.throwable) }
                )
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
            maxImageSize
                ?.also { maxImageSize -> builder.size(maxImageSize) }
                ?: { builder.size(Size.ORIGINAL) }
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth(),
                model = builder.build(),
                fallback = painterResource(Res.drawable.icon_emergency_home_24px),
                error = painterResource(Res.drawable.icon_emergency_home_24px),
                contentDescription = newsItem.imageCaption,
                contentScale = ContentScale.FillWidth,
            )
            if (newsItem.imageCaption.isNotEmpty()) {
                Text(
                    text = newsItem.imageCaption,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun NewsArticleMenuBar(
    uriHandler: UriHandler,
    currentNewsItem: NewsItem,
    currentNewsArticle: FullArticle?,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(5.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicatorButton(
            modifier = Modifier,
            width = 50.dp,
            height = 50.dp,
            leadingIcon = painterResource(Res.drawable.icon_arrow_back_24px),
            toolTip = stringResource(Res.string.tooltip_back),
        ) {
            onAction(NewsHomeReaderAction.OnNewsItemBackClicked())
        }

        IndicatorButton(
            modifier = Modifier,
            width = 50.dp,
            height = 50.dp,
            leadingIcon = painterResource(Res.drawable.icon_link_24px),
            toolTip = stringResource(Res.string.tooltip_open_link),
        ) {
            uriHandler.openUri(currentNewsItem.link)
        }

        currentNewsArticle?.discussionUrl?.also { link ->
            IndicatorButton(
                modifier = Modifier,
                width = 50.dp,
                height = 50.dp,
                leadingIcon = painterResource(Res.drawable.icon_chat_24px),
                toolTip = stringResource(Res.string.tooltip_open_chat),
            ) {
                uriHandler.openUri(
                    makeUrlAbsolute(
                        currentNewsItem.link,
                        link
                    )
                )
            }
        }
    }
}

@Composable
private fun MediaItemButtons(
    modifier: Modifier,
    mediaItems: List<MediaItem>,
    uriHandler: UriHandler,
    currentNewsItem: NewsItem
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        mediaItems.forEach { mediaItem ->
            if (mediaItem.url?.isNotEmpty() == true) {
                IndicatorButton(
                    modifier = Modifier,
                    text = mediaItem.headline,
                    textStyle = MaterialTheme.typography.bodySmall,
                    buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    maxLines = Int.MAX_VALUE,
                    toolTip = mediaItem.description,
                    width = 200.dp,
                    height = 100.dp,
                            leadingIcon = if (mediaItem.type == MediaType.video) painterResource(Res.drawable.icon_videocam_24px) else  painterResource(Res.drawable.icon_speaker_2_24px)
                ) {
                    uriHandler.openUri(
                        makeUrlAbsolute(
                            currentNewsItem.link,
                            mediaItem.url
                        )
                    )
                }
            }
        }
    }
}

private fun makeUrlAbsolute(
    absoluteUrl: String,
    relativeUrl: String
): String {
    val rel = URI(relativeUrl)
    return if (!rel.isAbsolute) {
        val abs = URI(absoluteUrl)
        val absoluteUrl = URI(abs.scheme, abs.userInfo, abs.host, abs.port, rel.path, rel.query, rel.fragment)
        absoluteUrl.toString()
    } else relativeUrl
}
