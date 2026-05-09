package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum

/**
 * Renders the news item card for a given newsfeed.
 */
@Composable
fun NewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    chunks: Int,
    displayTheme: DisplayThemeEnum,
    maxWidth: Dp,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    connectivityManager: ConnectivityManager,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val lastValidRowData = remember { mutableStateOf<List<List<NewsItem>>>(emptyList()) }

    val rowData = remember(state.visibleNewsItems, chunks) {
        if (state.visibleNewsItems.isNotEmpty() || state.allowClearVisibleNewsItems) {
            val newData = state.visibleNewsItems.chunked(chunks)
            lastValidRowData.value = newData
            newData
        } else {
            lastValidRowData.value
        }
    }

    if (maxWidth > 600.dp) {
        HorizontalNewsFeeds(
            state = state,
            scrollPosition = scrollPosition,
            displayTheme = displayTheme,
            connectivityManager = connectivityManager,
            maxWidth = maxWidth,
            rowData = rowData,
            maxImageSize = maxImageSize,
            settings = settings,
            uriHandler = uriHandler,
            chunks = chunks,
            onCommonAction = onCommonAction,
            onAction = onAction,
        )
    } else {
        VerticalNewsFeeds(
            scrollPosition = scrollPosition,
            state = state,
            connectivityManager = connectivityManager,
            maxWidth = maxWidth,
            rowData = rowData,
            maxImageSize = maxImageSize,
            settings = settings,
            uriHandler = uriHandler,
            chunks = chunks,
            onCommonAction = onCommonAction,
            onAction = onAction
        )
    }
}
