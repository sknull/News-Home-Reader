package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.compose.resources.icon_volume_up_24px
import de.visualdigits.newshomereader.domain.model.unified.MediaItem
import de.visualdigits.newshomereader.domain.model.unified.MediaType
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemImage
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource
import java.time.format.DateTimeFormatter

@Composable
fun MediaItemButtons(
    modifier: Modifier = Modifier,
    mediaItems: List<MediaItem>,
    uriHandler: UriHandler,
    newsItem: NewsItem
) {
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
                    IndicatorButton(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall),
                        padding = 0.dp,
                        textStyle = MaterialTheme.typography.bodySmall,
                        buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
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
                                        NewsItemImage(
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
                                        text = "${mediaItem.uploadDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
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
