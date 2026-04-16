package de.visualdigits.common.presentation.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun PlatformLazyVerticalScrollbar(
    modifier: Modifier,
    scrollState: LazyListState,
    interactionSource: MutableInteractionSource
) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState = scrollState),
        modifier = modifier,
        style = defaultScrollbarStyle().copy(
            unhoverColor = Color.White.copy(alpha = 0.6f),
            hoverColor = Color.White.copy(alpha = 0.8f)
        ),
        interactionSource = interactionSource
    )
}
