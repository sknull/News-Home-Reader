package de.visualdigits.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.model.CommonAction

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
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState(scrollPosition[scrollbarId]?.first?:0)
    LaunchedEffect(scrollState.value) {
        onCommonAction(CommonAction.OnScrollPositionChange(scrollbarId, scrollState.value))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = boxModifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(space)
                .verticalScroll(scrollState)
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(space)
        ) {
            rows().forEach { row ->
                row.second()
            }
        }

        PlatformVerticalScrollbar(
            interactionSource = interactionSource,
            modifier = scrollbarModifier
                .align(Alignment.CenterEnd),
            scrollState = scrollState
        )
    }
}
