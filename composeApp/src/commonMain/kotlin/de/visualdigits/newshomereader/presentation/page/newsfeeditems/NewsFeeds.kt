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
    displayTheme: DisplayThemeEnum,
    maxWidth: Dp,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    connectivityManager: ConnectivityManager,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val chunks = when {
        maxWidth > 1500.dp -> 4
        maxWidth > 1000.dp -> 3
        maxWidth > 500.dp -> 2
        else -> 1
    }
    val lastValidRowData = remember { mutableStateOf<List<Pair<String, List<NewsItem>>>>(emptyList()) }
    val rowData = remember(state.visibleNewsItems, chunks) {
        if (state.visibleNewsItems.isNotEmpty()) {
            val newData = state.visibleNewsItems.chunked(chunks).map { rowItems ->
                val rowKey = rowItems.joinToString("_") { it.identifier }
                rowKey to rowItems
            }
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

