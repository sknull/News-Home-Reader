package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_emergency_home_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import java.time.format.DateTimeFormatter


@Composable
fun NewsItemCard(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    maxHeight: Dp,
    newsItem: NewsItem,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

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
    ) {
        Column(
            modifier = Modifier
        ) {
            var image = newsItem.image
            if (image.isEmpty()) {
                image = newsItem.newsArticle?.articleImage?:""
            }
            if (image.isNotEmpty()) {
                val builder = ImageRequest
                    .Builder(LocalPlatformContext.current)
                    .data(image)
                    .listener(
                        onStart = {},
                        onSuccess = { _, _ -> },
                        onCancel = {},
                        onError = { _, result -> Logger.e("Image load failed: $image", result.throwable) }
                    )
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                state.maxImageSize
                    ?.also { maxImageSize -> builder.size(maxImageSize) }
                    ?: { builder.size(Size.ORIGINAL) }
                AsyncImage(
                    modifier = Modifier,
                    contentScale = ContentScale.FillWidth,
                    model = builder.build(),
                    fallback = painterResource(Res.drawable.icon_emergency_home_24px),
                    error = painterResource(Res.drawable.icon_emergency_home_24px),
                    contentDescription = newsItem.imageCaption
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.shapes.gap),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                Text(
                    text = "${newsItem.updated.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall
                )

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
