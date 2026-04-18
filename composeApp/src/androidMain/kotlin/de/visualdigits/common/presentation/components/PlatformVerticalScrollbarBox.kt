package de.visualdigits.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.style.gap
import kotlinx.coroutines.flow.collectLatest

@Composable
actual fun PlatformVerticalScrollbarBox(
    boxModifier: Modifier,
    scrollbarModifier: Modifier,
    scrollbarId: String,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onAction: (NewsHomeReaderAction) -> Unit,
    rows: () -> List<Pair<String, @Composable () -> Unit>>
) {
    val items = rows()

    if (items.isNotEmpty()) {
        val lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = scrollPosition[scrollbarId]?.first?:0,
            initialFirstVisibleItemScrollOffset = scrollPosition[scrollbarId]?.second?:0
        )

        LaunchedEffect(lazyListState) {
            snapshotFlow {
                // Wir beobachten zwei Werte gleichzeitig
                lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
            }
                .collectLatest { (index, offset) ->
                    // Wir schicken beide Werte (als Pair) an dein ViewModel
                    onAction(NewsHomeReaderAction.OnScrollPositionChange(scrollbarId, index, offset))
                }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = boxModifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(MaterialTheme.shapes.gap),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                state = lazyListState
            ) {
                items(
                    items = rows(),
                    key = { row -> row.first }
                ) {(_, rowContent) ->
                    rowContent()
                }
            }
        }
    } else {
        Box(Modifier.fillMaxSize())
    }
}
