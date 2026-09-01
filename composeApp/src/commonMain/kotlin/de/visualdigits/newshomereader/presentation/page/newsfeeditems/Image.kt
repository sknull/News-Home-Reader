package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_hourglass_top_24px
import org.jetbrains.compose.resources.painterResource

@Composable
fun Image(
    modifier: Modifier = Modifier,
    url: String,
    width: Dp? = null,
    height: Dp? = null,
    contentScale: ContentScale = ContentScale.FillWidth,
    contentDescription: String? = null,
    maxImageSize: Int?,
    showLoadingIcon: Boolean = true
) {

    val context = LocalPlatformContext.current

    val request = remember(url, maxImageSize) {
        ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .apply {
                if (maxImageSize != null) size(maxImageSize) else size(Size.ORIGINAL)
            }
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        var isLoading by remember { mutableStateOf(showLoadingIcon) }

        AsyncImage(
            modifier = Modifier
                .conditional(width != null) { width(width!!) }
                .conditional(height != null) { height(height!!) }
                .conditional(width == null && height == null) { fillMaxWidth() },
            contentScale = contentScale,
            model = request,
            contentDescription = contentDescription,
            onState = { state ->
                isLoading = showLoadingIcon && state is AsyncImagePainter.State.Loading
            }
        )

        if (isLoading) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(Res.drawable.icon_hourglass_top_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
