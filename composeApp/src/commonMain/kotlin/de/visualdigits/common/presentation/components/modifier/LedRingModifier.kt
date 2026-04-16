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
import de.visualdigits.common.domain.util.copyFactor
import de.visualdigits.common.domain.util.toHsvColor

@Composable
fun Modifier.ledRing(
    width: Dp,
    height: Dp,
    borderSize: Dp = 1.dp,
    ringSize: Dp = 5.dp,
    ringColor: Color? = null,
    buttonColor: Color? = null,
    flatLook: Boolean = false,
    hoverColor: Color = Color.White.copy(alpha = 0.2f),
    horizontalColors: List<Color>? = null,
    shape: Shape,
    borderAlpha: Float = 0.4f,
    isSelected: Boolean = false,
    isHovered: Boolean = false
): Modifier {
    return drawWithCache {
        val widthPx = width.toPx()
        val heightPx = height.toPx()
        val ringWidth = ringSize.toPx()

        val outerOffset = 4.0f
        val ringOffset = 4.0f + ringWidth / 2.0f
        val innerOffset = 4.0f + ringWidth

        val insetSizeRing = Size(widthPx - outerOffset * 2.0f - ringWidth, heightPx - outerOffset * 2.0f - ringWidth)

        val ringOutline = shape.createOutline(
            size = insetSizeRing,
            layoutDirection = layoutDirection,
            density = this@drawWithCache
        )
        val ringBrush = if (horizontalColors != null && isSelected) {
            if (horizontalColors.size > 1) {
                Brush.horizontalGradient(
                    colors = horizontalColors,
                    startX = outerOffset,
                    endX = widthPx - outerOffset * 2.0f - ringWidth
                )
            } else {
                null
            }
        } else {
            null
        }
        val finalRingColor = if (horizontalColors?.size == 1 && isSelected) {
            horizontalColors.first()
        } else if (isSelected) {
            ringColor
        } else {
            Color(0xff555555)
        }
        val ringOverlayBrush = Brush.verticalGradient(
            colorStops = arrayOf(
                0.0f to Color.White.copy(alpha = 0.4f), // top
                0.2f to Color.Transparent,
                0.7f to Color.Transparent,
                0.8f to Color.Black.copy(alpha = 0.4f), // bottom
            )
        )

        val innerOutline = shape.createOutline(
            size = Size(
                width = widthPx - outerOffset * 2.0f - ringWidth * 2.0f,
                height = heightPx - outerOffset * 2.0f - ringWidth * 2.0f
            ), layoutDirection = layoutDirection, density = this@drawWithCache
        )
        val innerBrush = createBevelBrush(
            heightPx = heightPx - outerOffset * 2.0f - ringWidth * 2.0f,
            widthPx = widthPx - outerOffset * 2.0f - ringWidth * 2.0f,
            borderAlpha = borderAlpha,
            inset = false
        )

        val glossClipPath = innerOutline.createPath()

        val glossBrush = Brush.radialGradient(
            center = Offset(x = widthPx / 2.0f, y = heightPx / 2.0f),
            radius = heightPx,
            colorStops = arrayOf(
                0.0f to Color.Transparent, // center
                0.45f to Color.White.copy(alpha = 0.2f),
                0.5f to Color.Transparent, // edge
            )
        )

        val outerBevelOutline = shape.createOutline(
            size = Size(
                width = widthPx - 2.0f,
                height = heightPx - 2.0f
            ), layoutDirection = layoutDirection, density = this@drawWithCache
        )
        val outerClipPath = outerBevelOutline.createPath()
        val outerBevelBrush = createBevelBrush(
            heightPx = heightPx - 2.0f,
            widthPx = widthPx - 2.0f,
            borderAlpha = borderAlpha,
            inset = false
        )

        val outerRingeBevelOutline = shape.createOutline(
            size = Size(
                width = widthPx - outerOffset * 2.0f,
                height = heightPx - outerOffset * 2.0f
            ), layoutDirection = layoutDirection, density = this@drawWithCache
        )
        val outerRingBevelBrush = createBevelBrush(
            heightPx = heightPx - outerOffset * 2.0f,
            widthPx = widthPx - outerOffset * 2.0f,
            borderAlpha = borderAlpha,
            inset = true
        )

        onDrawWithContent {
            clipPath(outerClipPath) {
                // background
                val finalButtonColor = buttonColor ?: Color.Black
                if (!flatLook) {
                    val hsvColor = finalButtonColor.toHsvColor()
                    val valueFactor = if (hsvColor.value < 0.5f) 1.5f else 0.5f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to finalButtonColor,
                                1.0f to finalButtonColor.copyFactor(valueFactor = valueFactor),
                            )
                        )
                    )
                } else {
                    drawRect(
                        color = finalButtonColor
                    )
                }

                this@onDrawWithContent.drawContent()

                // ring
                if (ringBrush != null) {
                    this@onDrawWithContent.draw(offsetX = ringOffset, outline = ringOutline, brush = ringBrush, width = ringWidth)
                } else if (finalRingColor != null) {
                    this@onDrawWithContent.draw(offsetX = ringOffset, outline = ringOutline, color = finalRingColor, width = ringWidth)
                }

                // ring overlay
                this@onDrawWithContent.draw(offsetX = ringOffset, outline = ringOutline, brush = ringOverlayBrush, width = ringWidth)

                // gloss
                if (!flatLook) {
                    withTransform({
                        translate(left = innerOffset, top = innerOffset)
                    }) {
                        clipPath(glossClipPath) {
                            withTransform({
                                scale(scaleX = 15.0f, scaleY = 1.0f)
                                rotate(-10.0f)
                                translate(left = -1f * innerOffset, top = -1f * innerOffset - heightPx / 2.3f)
                            }) {
                                drawCircle(
                                    brush = glossBrush
                                )
                            }
                        }
                    }
                }

                // outer ring bevel
                if (!flatLook) {
                    this@onDrawWithContent.draw(
                        offsetX = outerOffset,
                        outline = outerRingeBevelOutline,
                        brush = outerRingBevelBrush,
                        width = borderSize.toPx()
                    )
                }

                // inner ring bevel
                this@onDrawWithContent.draw(offsetX = innerOffset, outline = innerOutline, brush = innerBrush, width = borderSize.toPx())

                // outer bevel
                this@onDrawWithContent.draw(offsetX = 1.0f, outline = outerBevelOutline, brush = outerBevelBrush, width = borderSize.toPx())
            }

            if (isHovered) {
                drawRect(
                    topLeft = Offset(1),
                    size = Size(width = widthPx - 2, height = heightPx - 2),
                    color = hoverColor,
                        blendMode = BlendMode.Screen
                )
            }
        }
    }
}
