package de.visualdigits.common.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformVerticalScrollbar(
    modifier: Modifier,
    scrollState: ScrollState,
    interactionSource: MutableInteractionSource
) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState = scrollState),
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
        style = defaultScrollbarStyle().copy(
            unhoverColor = MaterialTheme.colorScheme.onSurface,
            hoverColor = MaterialTheme.colorScheme.inverseSurface
        ),
        interactionSource = interactionSource
    )
}
