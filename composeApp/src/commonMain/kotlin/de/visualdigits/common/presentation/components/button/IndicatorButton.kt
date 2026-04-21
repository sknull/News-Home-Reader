package de.visualdigits.common.presentation.components.button

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.PlatformToolTip
import de.visualdigits.common.presentation.components.modifier.indicator
import de.visualdigits.common.presentation.components.modifier.ledRing
import de.visualdigits.common.presentation.components.platformFocus
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.add_hint
import de.visualdigits.newshomereader.presentation.style.buttonsFlat
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndicatorButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    maxLines: Int = 1,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textColorDisabled: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    textAlign: TextAlign = TextAlign.Center,
    toolTip: String? = null,
    width: Dp = 160.dp,
    height: Dp = 50.dp,
    padding: Dp = 5.dp,
    buttonColor: Color? = MaterialTheme.colorScheme.surface,
    indicatorPosition: Alignment? = null,
    indicatorColor: Color? = null,
    flatLook: Boolean = MaterialTheme.shapes.buttonsFlat,
    horizontalColors: List<Color>? = null,
    hoverColor: Color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
    shape: Shape = MaterialTheme.shapes.extraSmall,
    selected: Boolean = false,
    leadingImage: (@Composable () -> Unit)? = null,
    leadingIcon: Painter? = null,
    leadingIconTint: Color = MaterialTheme.colorScheme.onSurface,
    leadingIconTintDisabled: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    trailingIcon: Painter? = null,
    trailingIconTint: Color = MaterialTheme.colorScheme.onSurface,
    trailingIconTintDisabled: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    var indicatorModifier = modifier
        .semantics { role = Role.Button }
        .width(width)
        .height(height)
    if (enabled) {
        indicatorModifier = indicatorModifier
            .hoverable(interactionSource = interactionSource)
            .pointerHoverIcon(PointerIcon.Hand)
            .platformFocus(onClick)
            .clickable(
                enabled = true,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    }

    if (indicatorPosition == Alignment.Center) {
        indicatorModifier = indicatorModifier
            .ledRing(
                width = width,
                height = height,
                shape = shape,
                buttonColor = buttonColor,
                ringColor = indicatorColor,
                flatLook = flatLook,
                hoverColor = hoverColor,
                horizontalColors = horizontalColors,
                isSelected = selected,
                isHovered = isHovered
            )
    } else {
        indicatorModifier = indicatorModifier
            .indicator(
                width = width,
                height = height,
                shape = shape,
                buttonColor = buttonColor,
                indicatorPosition = indicatorPosition,
                indicatorColor = indicatorColor,
                flatLook = flatLook,
                hoverColor = hoverColor,
                horizontalColors = horizontalColors,
                isSelected = selected,
                isHovered = isHovered
            )
    }

    Box(
        modifier = indicatorModifier,
        contentAlignment = Alignment.Center
    ) {
        val paddings = when (indicatorPosition) {
            Alignment.TopCenter -> listOf(padding * 3, padding, padding, padding)
            Alignment.BottomCenter -> listOf(padding, padding, padding * 3, padding)
            Alignment.CenterStart -> listOf(padding, padding, padding, padding * 3)
            Alignment.CenterEnd -> listOf(padding, padding * 3, padding, padding)
            Alignment.Center -> listOf(padding * 3, padding * 3, padding * 3, padding * 3)
            else -> listOf(padding, padding, padding, padding)
        }
        PlatformToolTip(toolTip, content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = paddings[0], end = paddings[1], bottom = paddings[2], start = paddings[3]),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            modifier = Modifier,
                            painter = leadingIcon,
                            contentDescription = stringResource(Res.string.add_hint),
                            tint = if (enabled) leadingIconTint else leadingIconTintDisabled
                        )
                        if (text != null) Spacer(Modifier.width(MaterialTheme.shapes.gap))
                    } else if (leadingImage != null) {
                        leadingImage()
                        if (text != null) Spacer(Modifier.width(MaterialTheme.shapes.gap))
                    }

                    if (text?.isNotEmpty() == true) {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = text,
                            textAlign = textAlign,
                            maxLines = maxLines,
                            overflow = if (maxLines == 1) TextOverflow.Ellipsis else TextOverflow.Clip,
                            softWrap = maxLines > 1,
                            style = textStyle,
                            color = if (enabled) textColor else textColorDisabled,
                        )
                    }

                    if (trailingIcon != null) {
                        if (text != null || leadingIcon != null) Spacer(Modifier.width(MaterialTheme.shapes.gap))
                        Icon(
                            modifier = Modifier,
                            painter = trailingIcon,
                            contentDescription = stringResource(Res.string.add_hint),
                            tint = if (enabled) trailingIconTint else trailingIconTintDisabled
                        )
                    }
                }
            }
        })
    }
}
