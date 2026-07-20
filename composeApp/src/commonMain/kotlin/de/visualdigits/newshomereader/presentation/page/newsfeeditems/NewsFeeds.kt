package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel

/**
 * Renders the news item card for a given newsfeed.
 */
@Composable
fun NewsFeeds(
    viewModel: NewsHomeReaderViewModel,
    state: NewsHomeReaderState,
    platformType: PlatformType,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
    columns: Int,
    maxWidth: Dp,
    maxHeight: Dp,
    maxImageSize: Int?,
    uriHandler: UriHandler,
    connectivityManager: ConnectivityManager,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val visibleNewsItems by viewModel.visibleNewsItems.collectAsStateWithLifecycle()
    val rowData by remember(visibleNewsItems, columns) {
        derivedStateOf {
            visibleNewsItems.chunked(columns)
        }
    }

    if (maxWidth > maxHeight && maxWidth > 600.dp) {
        NewsFeedsLandscape(
            state = state,
            platformType = platformType,
            scrollPosition = scrollPosition,
            connectivityManager = connectivityManager,
            maxWidth = maxWidth,
            rowData = rowData,
            maxImageSize = maxImageSize,
            uriHandler = uriHandler,
            columns = columns,
            onCommonAction = onCommonAction,
            onAction = onAction,
        )
    } else {
        NewsFeedsPortrait(
            state = state,
            platformType = platformType,
            scrollPosition = scrollPosition,
            connectivityManager = connectivityManager,
            maxWidth = maxWidth,
            rowData = rowData,
            maxImageSize = maxImageSize,
            uriHandler = uriHandler,
            columns = columns,
            onCommonAction = onCommonAction,
            onAction = onAction
        )
    }
}
