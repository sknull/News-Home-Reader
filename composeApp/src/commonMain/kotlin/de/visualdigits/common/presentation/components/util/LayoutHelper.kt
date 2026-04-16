package de.visualdigits.common.presentation.components.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp


@Composable
fun minimizedLabelHalfHeight(
    textStyle: TextStyle,
): Dp {
    val compositionLocalValue = textStyle.lineHeight
    val value = if (compositionLocalValue.isSp) compositionLocalValue else 16.sp
    return with(LocalDensity.current) { value.toDp() / 2 }
}
