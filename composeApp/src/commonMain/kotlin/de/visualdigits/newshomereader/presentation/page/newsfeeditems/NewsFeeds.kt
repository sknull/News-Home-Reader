package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap

/**
 * Renders the news item card for a given newsfeed.
 */
@Composable
fun NewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    displayTheme: DisplayThemeEnum,
    screenWidth: Dp,
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

    val rowDataFiltered = remember(state.filteredNewsItems) {
        state.filteredNewsItems.distinct().sortedByDescending { newsItem -> newsItem.updated }
    }

    if (maxWidth > 600.dp) {
        HorizontalNewsFeeds(
            state = state,
            scrollPosition = scrollPosition,
            displayTheme = displayTheme,
            connectivityManager = connectivityManager,
            screenWidth = screenWidth,
            maxWidth = maxWidth,
            rowData = rowData,
            rowDataFiltered = rowDataFiltered,
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
            screenWidth = screenWidth,
            maxWidth = maxWidth,
            rowData = rowData,
            rowDataFiltered = rowDataFiltered,
            maxImageSize = maxImageSize,
            settings = settings,
            uriHandler = uriHandler,
            chunks = chunks,
            onCommonAction = onCommonAction,
            onAction = onAction
        )
    }
}
