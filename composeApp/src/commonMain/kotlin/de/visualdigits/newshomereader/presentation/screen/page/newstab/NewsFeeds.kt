package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun NewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Int>,
    maxWidth: Dp,
    maxImageSize: Int?,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    if (maxWidth > 600.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.shapes.gap),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        ) {
            NewsFeedsNavigation(state, true, onAction)

            NewsItemsList(
                state = state,
                scrollPosition = scrollPosition,
                isLandscape = true,
                maxWidth = maxWidth - 200.dp - MaterialTheme.shapes.gap * 2,
                maxImageSize = maxImageSize,
                onAction = onAction,
                connectivityManager = connectivityManager
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.shapes.gap),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        ) {
            NewsFeedsNavigation(state, false, onAction)

            NewsItemsList(
                state = state,
                scrollPosition = scrollPosition,
                isLandscape = false,
                maxWidth = maxWidth,
                maxImageSize = maxImageSize,
                onAction = onAction,
                connectivityManager = connectivityManager
            )
        }
    }
}
