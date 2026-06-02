package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.compose.resources.icon_volume_up_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.MediaType
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.Image
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource
import java.time.format.DateTimeFormatter

@Composable
fun MediaItemButtons(
    state: NewsHomeReaderState,
    modifier: Modifier = Modifier,
    mediaItems: List<MediaItem>,
    uriHandler: UriHandler,
    newsItem: NewsItem
) {
    val buttonColor = remember { (state.settings?.get<HsvColor>(SK.buttonColor) ?: BUTTON_COLOR_DEFAULT).toComposeColor() }

    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        mediaItems
            .sortedByDescending { mi -> mi.uploadDate }
            .forEach { mediaItem ->
                if (mediaItem.url?.isNotEmpty() == true) {
                    Box(
                        modifier = modifier
                            .dropShadow(
                                shape = RoundedCornerShape(20.dp),
                                shadow = Shadow(
                                    radius = 6.dp,
                                    spread = 2.dp,
                                    color = Color.Black.copy(alpha = 0.2f),
                                    offset = DpOffset((-5).dp, 5.dp)
                                )
                            )
                    ) {
                        IndicatorButton(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall),
                            padding = 0.dp,
                            textStyle = MaterialTheme.typography.bodySmall,
                            buttonColor = buttonColor,
                            maxLines = Int.MAX_VALUE,
                            width = 200.dp,
                            height = 200.dp,
                            leadingImage = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                                ) {
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

                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp),
                                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                                    ) {
                                        Text(
                                            text = "${mediaItem.uploadDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            if (mediaItem.type == MediaType.video) {
                                                Icon(
                                                    painter = painterResource(Res.drawable.icon_videocam_24px),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )

                                            } else {
                                                Icon(
                                                    painter = painterResource(Res.drawable.icon_volume_up_24px),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Text(
                                                text = mediaItem.headline ?: "",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            uriHandler.openUri(
                                makeUrlAbsolute(
                                    newsItem.link,
                                    mediaItem.url
                                )
                            )
                        }
                    }
                }
            }
    }
}
