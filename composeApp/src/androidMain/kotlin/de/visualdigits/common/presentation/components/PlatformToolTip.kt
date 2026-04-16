package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Composable
actual fun PlatformToolTip(
    text: String?,
    textStyle: TextStyle,
    modifier: Modifier,
    shadowSize: Dp,
    content: @Composable () -> Unit
) {
    content()
}
