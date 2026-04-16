package de.visualdigits.common.presentation.components.modifier

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

fun Outline.createPath(): Path = when (this) {
    is Outline.Generic -> path
    is Outline.Rectangle -> Path().apply { addRect(rect) }
    is Outline.Rounded -> Path().apply { addRoundRect(roundRect) }
}

fun ContentDrawScope.draw(
    offsetX: Float,
    offsetY: Float? = null,
    outline: Outline,
    color: Color? = null,
    brush: Brush? = null,
    width: Float
) {
    withTransform({
        translate(left = offsetX, top = offsetY?:offsetX)
    }) {
        if (color != null) {
            drawOutline(
                outline = outline,
                color = color,
                style = Stroke(width = width)
            )
        } else if (brush != null) {
            drawOutline(
                outline = outline,
                brush = brush,
                style = Stroke(width = width)
            )
        }
    }
}

fun createBevelBrush(
    heightPx: Float,
    widthPx: Float,
    borderAlpha: Float,
    inset: Boolean
): Brush {
    val offsetColors = heightPx / widthPx
    val offsetBorder = widthPx / (widthPx + heightPx)

    val color1 = Color.White.copy(alpha = borderAlpha)
    val color2 = Color.Black.copy(alpha = borderAlpha)
    val (startColor, endColor) = if (inset) {
        Pair(color2, color1)
    } else {
        Pair(color1, color2)
    }

    val brush = Brush.linearGradient(
        colorStops = arrayOf(
            (offsetColors - 0.00f).coerceAtLeast(0.0f) to startColor,
            (offsetColors - 0.01f).coerceAtLeast(0.0f) to startColor,
            (offsetColors + 0.01f).coerceAtMost(1.0f) to endColor,
            (offsetColors + 1.00f).coerceAtMost(1.0f) to endColor,
        ),
        start = Offset(offsetBorder * 100.0f, 0f),
        end = Offset(100.0f, 100.0f)
    )

    return brush
}
