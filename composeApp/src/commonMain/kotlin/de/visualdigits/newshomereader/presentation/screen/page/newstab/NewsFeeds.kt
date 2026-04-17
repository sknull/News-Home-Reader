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
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun NewsFeeds(
    maxWidth: Dp,
    maxHeight: Dp,
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
            NewsFeedsNavigation(true, onAction)

            NewsItemsList(
                isLandscape = true,
                maxWidth = maxWidth - 200.dp - MaterialTheme.shapes.gap * 2,
                maxHeight = maxHeight,
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
            NewsFeedsNavigation(false, onAction)

            NewsItemsList(
                isLandscape = false,
                maxWidth = maxWidth,
                maxHeight = maxHeight - 400.dp,
                onAction = onAction,
                connectivityManager = connectivityManager
            )
        }
    }
}
