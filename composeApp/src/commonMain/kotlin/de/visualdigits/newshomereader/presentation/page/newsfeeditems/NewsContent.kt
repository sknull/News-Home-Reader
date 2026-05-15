package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.circuit_board_squared
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.article.NewsArticleCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.imageResource

@Composable
fun NewsContent(
    state: NewsHomeReaderState,
    chunks: Int,
    viewModel: NewsHomeReaderViewModel,
    maxWidth: Dp,
    maxHeight: Dp,
    maxImageSize: Int,
    uriHandler: UriHandler,
    connectivityManager: ConnectivityManager,
    displayTheme: DisplayThemeEnum,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        if (state.currentNewsArticle != null && state.currentNewsItem != null) {
            NewsArticleCard(
                scrollPosition = viewModel.scrollPosition,
                maxWidth = maxWidth,
                maxImageSize = maxImageSize,
                newsItem = state.currentNewsItem,
                newsArticle = state.currentNewsArticle,
                settings = state.settings,
                uriHandler = uriHandler,
                state = state,
                onCommonAction = onCommonAction,
                onAction = onAction,
                connectivityManager = connectivityManager
            )
        } else {
            NewsFeeds(
                state = state,
                scrollPosition = viewModel.scrollPosition,
                chunks = chunks,
                displayTheme = displayTheme,
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                maxImageSize = maxImageSize,
                uriHandler = uriHandler,
                connectivityManager = connectivityManager,
                onCommonAction = onCommonAction,
                onAction = onAction
            )
        }
    }
}
