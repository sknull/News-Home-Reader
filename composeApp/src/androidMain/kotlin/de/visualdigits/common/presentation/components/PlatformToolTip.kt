package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Composable
actual fun PlatformToolTip(
    text: String?,
    textStyle: TextStyle,
    modifier: Modifier,
    shadowSize: Dp,
    space: Dp,
    backgroundColor: Color,
    shape: Shape,
    content: @Composable () -> Unit
) {
    content()
}
