package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.color.HsvColor
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.common.presentation.util.openUriSafely
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_photo_24px
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.compose.resources.icon_volume_up_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.MediaType
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.parseDuration
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.Image
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import eu.iamkonstantin.kotlin.gadulka.GadulkaPlayer
import org.jetbrains.compose.resources.painterResource

@Composable
fun MediaItemButtons(
    modifier: Modifier = Modifier,
    viewModel: NewsHomeReaderViewModel,
    player: GadulkaPlayer,
    mediaItems: List<MediaItem>,
    uriHandler: UriHandler,
    newsItem: NewsItem,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val buttonColor = remember { (settings?.get<HsvColor>(SK.buttonColor) ?: BUTTON_COLOR_DEFAULT).toComposeColor() }
    val hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        mediaItems
            .sortedByDescending { mi -> mi.uploadDate }
            .forEach { mediaItem ->
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()

                if (mediaItem.url?.isNotEmpty() == true) {
                    val urlAbsolute = makeUrlAbsolute(
                        newsItem.link,
                        mediaItem.url
                    )
                    var isPlaying by remember { mutableStateOf(false) }

                    Box { // container to hold hover glass pane
                        Column(
                            modifier = modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .width(200.dp)
                                .height(200.dp)
                                .background(buttonColor)
                                .hoverable(interactionSource = interactionSource)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable(
                                    enabled = true,
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        if (mediaItem.type == MediaType.audio && urlAbsolute.contains(".mp3", ignoreCase = true)) {
                                            if (!isPlaying) {
                                                isPlaying = true
                                                player.play(urlAbsolute)
                                            } else {
                                                isPlaying = false
                                                player.stop()
                                                player.release()
                                            }
                                        } else if (mediaItem.type == MediaType.image) {
                                                onAction(NewsHomeReaderAction.OnShowArticleImage(
                                                    url = urlAbsolute,
                                                    title = mediaItem.headline
                                                ))
                                        } else {
                                            uriHandler.openUriSafely(urlAbsolute)
                                        }
                                    }
                                ),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            // teaser image
                            if (mediaItem.thumbnails.isNotEmpty()) {
                                val thumbnail = mediaItem.thumbnails
                                    .minBy { ti -> ti.width ?: 0 }

                                val url = thumbnail.url.firstOrNull()
                                if (url != null) {
                                    Image(
                                        url = url,
                                        height = 90.dp,
                                        contentDescription = thumbnail.description ?: "",
                                        maxImageSize = thumbnail.width
                                    )
                                }
                            }

                            // publish date
                            mediaItem.uploadDate?.let { ud ->
                                Text(
                                    text = ud.format("dd.MM.yyyy HH:mm"),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // indicator
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                                verticalAlignment = Alignment.Top
                            ) {
                                when (mediaItem.type) {
                                    MediaType.video -> Icon(
                                        painter = painterResource(Res.drawable.icon_videocam_24px),
                                        contentDescription = null,
                                        tint = if (isHovered || isPlaying) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface
                                    )

                                    MediaType.audio -> Icon(
                                        painter = painterResource(Res.drawable.icon_volume_up_24px),
                                        contentDescription = null,
                                        tint = if (isHovered || isPlaying) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface
                                    )

                                    MediaType.image -> Icon(
                                        painter = painterResource(Res.drawable.icon_photo_24px),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )

                                    else -> {}
                                }

                                // headline and duration
                                val durationString = mediaItem.duration?.parseDuration()?.let { d -> " [$d]" } ?: ""
                                Text(
                                    text = "${mediaItem.headline ?: ""}$durationString",
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .conditional(isHovered) {
                                    background(hoverColor) }
                        )
                    }
                }
            }
    }
}
