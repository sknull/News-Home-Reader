package de.visualdigits.common.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalFoundationApi::class)
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
    if (text != null) {
        TooltipArea(
            modifier = modifier,
            tooltip = {
                Surface(
                    modifier = Modifier
                        .shadow(
                            elevation = shadowSize,
                            shape = shape,
                            spotColor = Color.Black,
                            ambientColor = Color.White
                        )
                        .padding(start = shadowSize, end = shadowSize / 2, top = shadowSize, bottom = shadowSize / 2)
                ) {
                    Text(
                        text = text,
                        style = textStyle,
                        modifier = Modifier
                            .background(backgroundColor)
                            .border(0.5.dp, Color.Black)
                            .padding(space)
                    )
                }
            },
            delayMillis = 600,
            content = content
        )
    } else {
        content()
    }
}
