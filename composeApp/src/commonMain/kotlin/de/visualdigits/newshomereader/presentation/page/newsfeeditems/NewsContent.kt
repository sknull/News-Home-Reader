package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.article.NewsArticleCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun NewsContent(
    state: NewsHomeReaderState,
    viewModel: NewsHomeReaderViewModel,
    screenWidth: Dp,
    mw: Dp,
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
                maxWidth = mw,
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
                displayTheme = displayTheme,
                screenWidth = screenWidth,
                maxWidth = mw,
                maxImageSize = maxImageSize,
                settings = state.settings,
                uriHandler = uriHandler,
                connectivityManager = connectivityManager,
                onCommonAction = onCommonAction,
                onAction = onAction
            )
        }
    }
}
