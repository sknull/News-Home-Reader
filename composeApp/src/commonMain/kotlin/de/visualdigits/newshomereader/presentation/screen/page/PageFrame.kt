package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.max

@Composable
fun PageFrame(
    onAction: (NewsHomeReaderAction) -> Unit,
    content: @Composable (Dp, Dp) -> Unit,
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

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
            onAction(NewsHomeReaderAction.UpdateMaxImageSize(state.settings, max(wPx, hPx)))
        }
        content(maxWidth, maxHeight)
    }
}
