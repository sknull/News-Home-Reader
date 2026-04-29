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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.presentation.model.CommonAction
import kotlinx.coroutines.flow.collectLatest

@Composable
actual fun PlatformVerticalScrollbarBox(
    boxModifier: Modifier,
    backgroundColor: Color,
    scrollbarModifier: Modifier,
    scrollbarId: String,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    collapsibleState: Map<String, Boolean>,
    onCommonAction: (CommonAction) -> Unit,
    space: Dp,
    rows: () -> List<Pair<String, @Composable () -> Unit>>
) {
    val items = rows()

    if (items.isNotEmpty()) {
        val lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = scrollPosition[scrollbarId]?.first?:0,
            initialFirstVisibleItemScrollOffset = scrollPosition[scrollbarId]?.second?:0
        )

        LaunchedEffect(items.size) { // Reagiert, wenn sich die Anzahl der Items (Menü an/aus) ändert
            if (collapsibleState["group_newsfeeds_navigation"] == true) {
                lazyListState.animateScrollToItem(0)
            }
        }
        LaunchedEffect(lazyListState) {
            snapshotFlow {
                // Wir beobachten zwei Werte gleichzeitig
                lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
            }
                .collectLatest { (index, offset) ->
                    // Wir schicken beide Werte (als Pair) an dein ViewModel
                    onCommonAction(CommonAction.OnScrollPositionChange(scrollbarId, index, offset))
                }
        }
        LazyColumn(
            modifier = boxModifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(space),
            verticalArrangement = Arrangement.spacedBy(space),
            state = lazyListState
        ) {
            items(
                items = rows(),
                key = { row -> row.first }
            ) {(_, rowContent) ->
                rowContent()
            }
        }
    } else {
        Box(Modifier.fillMaxSize())
    }
}
