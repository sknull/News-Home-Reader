package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.article.NewsArticleCard
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun NewsContent(
    viewModel: NewsHomeReaderViewModel,
    state: NewsHomeReaderState,
    platformType: PlatformType,
    columns: Int,
    maxWidth: Dp,
    maxHeight: Dp,
    maxImageSize: Int,
    uriHandler: UriHandler,
    connectivityManager: ConnectivityManager,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        if (state.currentNewsArticle != null && state.currentNewsItem != null) {
            NewsArticleCard(
                viewModel = viewModel,
                platformType = platformType,
                scrollPosition = viewModel.scrollPosition,
                maxWidth = maxWidth,
                maxImageSize = maxImageSize,
                newsItem = state.currentNewsItem,
                newsArticle = state.currentNewsArticle,
                settings = settings,
                uriHandler = uriHandler,
                state = state,
                onCommonAction = onCommonAction,
                onAction = onAction,
                connectivityManager = connectivityManager
            )
        } else {
            NewsFeeds(
                viewModel = viewModel,
                state = state,
                platformType = platformType,
                scrollPosition = viewModel.scrollPosition,
                columns = columns,
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
