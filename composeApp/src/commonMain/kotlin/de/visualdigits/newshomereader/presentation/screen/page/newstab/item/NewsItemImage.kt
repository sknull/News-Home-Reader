package de.visualdigits.newshomereader.presentation.screen.page.newstab.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_hourglass_top_24px
import de.visualdigits.common.presentation.components.util.conditional
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsItemImage(
    modifier: Modifier = Modifier,
    url: String,
    width: Dp? = null,
    height: Dp? = null,
    contentDescription: String,
    maxImageSize: Int?
) {

    val log = Logger.withTag("NewsItemImage")

    val context = LocalPlatformContext.current
    val request = remember(url, maxImageSize) {
        val builder = ImageRequest
            .Builder(context)
            .data(url)
            .listener(
                onStart = {},
                onSuccess = { _, _ -> },
                onCancel = {},
                onError = { _, result -> log.e("Image load failed: $url", result.throwable) }
            )
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
        maxImageSize
            ?.also { maxImageSize -> builder.size(maxImageSize) }
            ?: run { builder.size(Size.ORIGINAL) }
        builder.build()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp),
            painter = painterResource(Res.drawable.icon_hourglass_top_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )

        AsyncImage(
            modifier = modifier
                .conditional(width != null) { width(width!!) }
                .conditional(height != null) { height(height!!) }
                .conditional(width == null && height == null) { fillMaxWidth() },
            contentScale = ContentScale.FillWidth,
            model = request,
            contentDescription = contentDescription
        )
    }
}
