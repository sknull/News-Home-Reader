package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_speaker_2_24px
import de.visualdigits.compose.resources.icon_videocam_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import java.time.format.DateTimeFormatter


@Composable
fun NewsItemCard(
    modifier: Modifier = Modifier,
    maxImageSize: Int?,
    newsItem: NewsItem,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
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
        Column(
            modifier = Modifier
        ) {
            var image = newsItem.image
            if (image.isEmpty()) {
                image = newsItem.newsArticle?.articleImage?:""
            }
            if (image.isNotEmpty()) {
                NewsItemImage(url = image, contentDescription = newsItem.imageCaption, maxImageSize = maxImageSize)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.shapes.gap),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${newsItem.updated.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.width(MaterialTheme.shapes.gap))
                    if (newsItem.newsArticle?.videoItems?.isNotEmpty() == true) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_videocam_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (newsItem.newsArticle?.audioItems?.isNotEmpty() == true) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_speaker_2_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = newsItem.summary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
