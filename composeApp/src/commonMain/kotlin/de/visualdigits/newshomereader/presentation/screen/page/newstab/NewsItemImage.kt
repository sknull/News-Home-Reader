package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_emergency_home_24px
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import org.jetbrains.compose.resources.painterResource

@Composable
fun ColumnScope.NewsItemImage(
    image: String,
    newsItem: NewsItem,
    maxImageSize: Int?
) {

    val log = kermitLogger()

    val context = LocalPlatformContext.current
    val request = remember(image, maxImageSize) {
        val builder = ImageRequest
            .Builder(context)
            .data(image)
            .listener(
                onStart = {},
                onSuccess = { _, _ -> },
                onCancel = {},
                onError = { _, result -> log.e("Image load failed: $image", result.throwable) }
            )
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
        maxImageSize
            ?.also { maxImageSize -> builder.size(maxImageSize) }
            ?: run { builder.size(Size.ORIGINAL) }
        builder.build()
    }

    AsyncImage(
        modifier = Modifier
            .fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
        model = request,
        fallback = painterResource(Res.drawable.icon_emergency_home_24px),
        error = painterResource(Res.drawable.icon_emergency_home_24px),
        contentDescription = newsItem.imageCaption
    )
}
