package de.visualdigits.newshomereader.presentation.screen.page.newstab.newsfeeds

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.screen.page.newstab.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.screen.page.newstab.newslist.NewsListMenuBar
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    displayTheme: DisplayThemeEnum,
    maxWidth: Dp,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val chunks = when {
        maxWidth > 1500.dp -> 4
        maxWidth > 1000.dp -> 3
        maxWidth > 500.dp -> 2
        else -> 1
    }
    val rowData = remember(state.visibleNewsItems, chunks) {
        state.visibleNewsItems.chunked(chunks).map { rowItems ->
            val rowKey = rowItems.joinToString("_") { it.identifier }
            rowKey to rowItems
        }
    }

    if (maxWidth > 600.dp) {
        HorizontalNewsFeeds(
            state = state,
            onAction = onAction,
            scrollPosition = scrollPosition,
            displayTheme = displayTheme,
            connectivityManager = connectivityManager,
            maxWidth = maxWidth,
            rowData = rowData,
            maxImageSize = maxImageSize,
            settings = settings,
            uriHandler = uriHandler,
            chunks = chunks
        )
    } else {
        VerticalNewsFeeds(
            scrollPosition = scrollPosition,
            onAction = onAction,
            state = state,
            connectivityManager = connectivityManager,
            maxWidth = maxWidth,
            rowData = rowData,
            maxImageSize = maxImageSize,
            settings = settings,
            uriHandler = uriHandler,
            chunks = chunks
        )
    }
}

