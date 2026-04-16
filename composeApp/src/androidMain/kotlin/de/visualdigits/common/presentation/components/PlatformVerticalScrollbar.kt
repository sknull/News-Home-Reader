package de.visualdigits.common.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformVerticalScrollbar(
    modifier: Modifier,
    scrollState: ScrollState,
    interactionSource: MutableInteractionSource
) {
    // not supported in android
}
