package de.visualdigits.common.presentation.components.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun Modifier.indicator(
    width: Dp,
    height: Dp,
    borderSize: Dp = 1.dp,
    indicatorSize: Dp = 5.dp,
    indicatorPosition: Alignment? = null,
    buttonColor: Color? = null,
    indicatorColor: Color? = null,
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
        val indicatorWidth = indicatorSize.toPx()

        val (indicatorOffsetX, indicatorOffsetY) = when (indicatorPosition) {
            Alignment.TopCenter -> Pair(10.0f, 7.0f)
            Alignment.BottomCenter -> Pair(10.0f, heightPx - indicatorWidth - 7.0f)
            Alignment.CenterStart -> Pair(7.0f, 10.0f)
            Alignment.CenterEnd -> Pair(widthPx - indicatorWidth - 7.0f, 10.0f)
            else -> Pair(0.0f, 0.0f)
        }

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

        val glossBrush = Brush.radialGradient(
            center = Offset(x = widthPx / 2.0f, y = heightPx / 2.0f),
            radius = heightPx,
            colorStops = arrayOf(
                0.0f to Color.Transparent, // center
                0.45f to Color.White.copy(alpha = 0.2f),
                0.5f to Color.Transparent, // edge
            )
        )

        val indicatorBrush = if (horizontalColors != null && isSelected) {
            if (horizontalColors.size > 1) {
                Brush.horizontalGradient(
                    colors = horizontalColors,
                    startX = indicatorOffsetX,
                    endX = widthPx - indicatorOffsetX * 2.0f - indicatorWidth
                )
            } else {
                null
            }
        } else {
            null
        }
        val finalIndicatorColor = if (horizontalColors?.size == 1 && isSelected) {
            horizontalColors.first()
        } else if (isSelected) {
            indicatorColor
        } else {
            Color(0xff555555)
        }

        val indicatorBevelOutline = when (indicatorPosition) {
            Alignment.TopCenter, Alignment.BottomCenter -> {
                shape.createOutline(
                    size = Size(
                        width = widthPx - indicatorOffsetX * 2.0f,
                        height = indicatorWidth
                    ), layoutDirection = layoutDirection, density = this@drawWithCache
                )
            }
            Alignment.CenterStart, Alignment.CenterEnd -> {
                shape.createOutline(
                    size = Size(
                        width = indicatorWidth,
                        height = heightPx - indicatorOffsetY * 2.0f
                    ), layoutDirection = layoutDirection, density = this@drawWithCache
                )
            }
            else -> null
        }
        val indicatorBevelBrush = when (indicatorPosition) {
            Alignment.TopCenter, Alignment.BottomCenter -> {
                createBevelBrush(
                    widthPx = widthPx - indicatorOffsetX * 2.0f,
                    heightPx = heightPx - 2.0f,
                    borderAlpha = borderAlpha,
                    inset = true
                )
            }
            Alignment.CenterStart, Alignment.CenterEnd -> {
                createBevelBrush(
                    widthPx = indicatorWidth,
                    heightPx = heightPx - indicatorOffsetY * 2.0f,
                    borderAlpha = borderAlpha,
                    inset = true
                )
            }
            else -> null
        }

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

                // gloss
                if (!flatLook) {
                    clipPath(outerClipPath) {
                        withTransform({
                            scale(scaleX = 15.0f, scaleY = 1.0f)
                            rotate(-10.0f)
                            translate(top = heightPx / -2.0f)
                        }) {
                            drawCircle(
                                brush = glossBrush
                            )
                        }
                    }
                }

                when (indicatorPosition) {
                    Alignment.TopCenter, Alignment.BottomCenter -> {
                        // indicator
                        if (indicatorBrush != null) {
                            drawRect(
                                topLeft = Offset(x = indicatorOffsetX, y = indicatorOffsetY),
                                size = Size(width = widthPx - indicatorOffsetX * 2.0f, height = indicatorWidth),
                                brush = indicatorBrush
                            )
                        } else if (finalIndicatorColor != null) {
                            drawRect(
                                topLeft = Offset(x = indicatorOffsetX, y = indicatorOffsetY),
                                size = Size(width = widthPx - indicatorOffsetX * 2.0f, height = indicatorWidth),
                                color = finalIndicatorColor
                            )
                        } else {
                        }
                    }
                    Alignment.CenterStart, Alignment.CenterEnd -> {
                        // indicator
                        if (indicatorBrush != null) {
                            drawRect(
                                topLeft = Offset(x = indicatorOffsetX, y = indicatorOffsetY),
                                size = Size(width = indicatorWidth, height = heightPx - indicatorOffsetY * 2.0f),
                                brush = indicatorBrush
                            )
                        } else if (finalIndicatorColor != null) {
                            drawRect(
                                topLeft = Offset(x = indicatorOffsetX, y = indicatorOffsetY),
                                size = Size(width = indicatorWidth, height = heightPx - indicatorOffsetY * 2.0f),
                                color = finalIndicatorColor
                            )
                        } else {
                        }
                    }
                    else -> {}
                }

                // indicator bevel
                if (indicatorBevelOutline != null) {
                    this@onDrawWithContent.draw(
                        offsetX = indicatorOffsetX,
                        offsetY = indicatorOffsetY,
                        outline = indicatorBevelOutline,
                        brush = indicatorBevelBrush,
                        width = borderSize.toPx()
                    )
                }

                // outer bevel
                if (!flatLook) {
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
}
