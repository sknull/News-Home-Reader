package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import kotlin.math.max

@Composable
fun PageFrame(
    settings: Settings?,
    onAction: (NewsHomeReaderAction) -> Unit,
    content: @Composable (Dp, Dp) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
    ) {
        val density = LocalDensity.current
        LaunchedEffect(maxWidth, maxHeight) {
            // Umrechnung von Dp in Pixel für Coil
            val wPx = with(density) { maxWidth.roundToPx() }
            val hPx = with(density) { maxHeight.roundToPx() }
            onAction(NewsHomeReaderAction.UpdateMaxImageSize(settings, max(wPx, hPx)))
        }
        content(maxWidth, maxHeight)
    }
}
