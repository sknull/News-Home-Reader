package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsArticleCard
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsFeeds
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun NewsContent(
    state: NewsHomeReaderState,
    viewModel: NewsHomeReaderViewModel,
    mw: Dp,
    maxImageSize: Int,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager,
    displayTheme: DisplayThemeEnum
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        if (state.currentNewsArticle != null) {
            state.currentNewsItem?.let { ni ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NewsArticleCard(
                        modifier = Modifier
                            .widthIn(max = 1000.dp),
                        scrollPosition = viewModel.scrollPosition,
                        maxWidth = mw,
                        maxImageSize = maxImageSize,
                        newsArticle = state.currentNewsArticle,
                        settings = state.settings,
                        uriHandler = uriHandler,
                        newsItem = ni,
                        onAction = onAction,
                        connectivityManager = connectivityManager
                    )
                }
            }
        } else {
            NewsFeeds(
                state = state,
                scrollPosition = viewModel.scrollPosition,
                displayTheme = displayTheme,
                maxWidth = mw,
                maxImageSize = maxImageSize,
                settings = state.settings,
                uriHandler = uriHandler,
                onAction = onAction,
                connectivityManager = connectivityManager
            )
        }
    }
}
