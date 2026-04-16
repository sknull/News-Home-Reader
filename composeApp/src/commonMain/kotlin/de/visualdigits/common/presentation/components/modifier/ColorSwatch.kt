package de.visualdigits.common.presentation.components.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorPickerController

@Composable
fun Modifier.colorSwatch(
    size: Dp,
    selectedColor: Color = Color.Transparent,
    controller: ColorPickerController? = null,
    hoverColor: Color = Color.White.copy(alpha = 0.2f),
    borderSize: Dp = 1.dp,
    borderAlpha: Float = 0.4f,
    shape: Shape,
    isHovered: Boolean = false
): Modifier {
    return drawWithCache {
        val color = controller?.selectedColor?.value ?: selectedColor
        val sizePx = size.toPx()
        val outerBevelOutline = shape.createOutline(
            size = Size(
                width = sizePx - 2.0f,
                height = sizePx - 2.0f
            ), layoutDirection = layoutDirection, density = this@drawWithCache
        )
        val outerClipPath = outerBevelOutline.createPath()
        val outerBevelBrush = createBevelBrush(
            heightPx = sizePx - 2.0f,
            widthPx = sizePx - 2.0f,
            borderAlpha = borderAlpha,
            inset = false
        )

        val glossBrush = Brush.radialGradient(
            center = Offset(x = sizePx / 2.0f, y = sizePx / 2.0f),
            radius = sizePx,
            colorStops = arrayOf(
                0.0f to Color.Transparent, // center
                0.45f to Color.White.copy(alpha = 0.2f),
                0.5f to Color.Transparent, // edge
            )
        )

        onDrawWithContent {
            clipPath(outerClipPath) {

                // background
                drawRect(color = color)

                // gloss
                clipPath(outerClipPath) {
                    withTransform({
                        scale(scaleX = 15.0f, scaleY = 1.0f)
                        rotate(-10.0f)
                        translate(top = sizePx / -2.0f)
                    }) {
                        drawCircle(
                            brush = glossBrush
                        )
                    }
                }

                // outer bevel
                this@onDrawWithContent.draw(offsetX = 1.0f, outline = outerBevelOutline, brush = outerBevelBrush, width = borderSize.toPx())

                if (isHovered) {
                    drawRect(
                        topLeft = Offset(1),
                        size = Size(width = sizePx - 2, height = sizePx - 2),
                        color = hoverColor,
                        blendMode = BlendMode.Screen
                    )
                }
            }
        }
    }
}
