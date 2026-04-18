package de.visualdigits.common.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
actual fun PlatformVerticalScrollbarBox(
    boxModifier: Modifier,
    scrollbarModifier: Modifier,
    scrollState: ScrollState,
    interactionSource: MutableInteractionSource,
    rows: () -> List<Pair<String, @Composable () -> Unit>>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = boxModifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(MaterialTheme.shapes.gap)
                .verticalScroll(scrollState)
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
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
