package de.visualdigits.common.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        LazyColumn(
            modifier = boxModifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(MaterialTheme.shapes.gap),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
        ) {
            items(
                items = rows(),
                key = { row -> row.first }
            ) {(_, rowContent) ->
                rowContent()
            }
        }
    }
}
