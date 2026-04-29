package de.visualdigits.common.presentation.components.container

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiPlatform
import de.visualdigits.common.presentation.components.androidPlatform
import de.visualdigits.common.presentation.components.platformFocus
import org.jetbrains.compose.resources.painterResource

@Composable
fun HorizontalCollapsibleBox(
    modifier: Modifier = Modifier,
    iconArrowRight: Painter,
    iconArrowDown: Painter,
    space: Dp = 8.dp,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.small,
    expandedWidth: Dp,
    height: Dp,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    isExpanded: Boolean,
    onStateChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    if (androidPlatform() == UiPlatform.UI_MODE_TYPE_TELEVISION) {
        HorizontalCollapsibleBoxTv(
            modifier = modifier,
            space = space,
            backgroundColor = backgroundColor,
            shape = shape,
            expandedWidth = expandedWidth,
            height = height,
            iconTint = iconTint,
            content = content
        )
    } else {
        HorizontalCollapsibleBoxFull(
            modifier = modifier,
            iconArrowRight = iconArrowRight,
            iconArrowDown = iconArrowDown,
            space = space,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedBorderColor = focusedBorderColor,
            backgroundColor = backgroundColor,
            shape = shape,
            expandedWidth = expandedWidth,
            height = height,
            iconTint = iconTint,
            onStateChange = onStateChange,
            isExpanded = isExpanded,
            content = content
        )
    }
}

@Composable
fun HorizontalCollapsibleBoxFull(
    modifier: Modifier = Modifier,
    iconArrowRight: Painter,
    iconArrowDown: Painter,
    space: Dp = 8.dp,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    backgroundColor: Color,
    shape: Shape,
    expandedWidth: Dp,
    height: Dp,
    isExpanded: Boolean,
    iconTint: Color = Color.White,
    onStateChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    var textfieldModifier = modifier
        .clip(shape)
        .height(height)
        .background(backgroundColor)
        .animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    textfieldModifier = if (isExpanded) {
        textfieldModifier.width(expandedWidth)
    } else {
        textfieldModifier.width(50.dp)
    }
    BasicTextField(
        modifier = textfieldModifier,
        value = "",
        onValueChange = { },
        readOnly = true,
        singleLine = false,
        decorationBox = { _ ->
            OutlinedTextFieldDefaults.DecorationBox(
                innerTextField = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(shape)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable {
                                onStateChange(!isExpanded)
                            },
                    ) {
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(top = space)
                                .clip(shape)
                                .width(50.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isExpanded) {
                                Icon(
                                    painter =iconArrowRight,
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            } else {
                                Icon(
                                    painter = iconArrowDown,
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            }
                        }

                        if (isExpanded) {
                            content()
                        }
                    }
                },
                visualTransformation = VisualTransformation.None,
                value = "",
                singleLine = false,
                enabled = true,
                isError = false,
                interactionSource = interactionSource,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = unfocusedBorderColor,
                    focusedBorderColor = focusedBorderColor,
                ),
                contentPadding = PaddingValues(top = 0.dp, end = 0.dp, bottom = 0.dp, start = 0.dp)
            ) {
                OutlinedTextFieldDefaults.Container(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = unfocusedBorderColor,
                        focusedBorderColor = focusedBorderColor
                    ),
                    shape = shape,
                )
            }
        }
    )
}

@Composable
fun HorizontalCollapsibleBoxTv(
    modifier: Modifier = Modifier,
    space: Dp = 8.dp,
    backgroundColor: Color,
    shape: Shape,
    expandedWidth: Dp,
    height: Dp,
    iconTint: Color = Color.White,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .width(expandedWidth)
            .height(height)
            .background(backgroundColor),
        horizontalArrangement = Arrangement.spacedBy(space)
    ) {
        Column(
            modifier = Modifier
                .width(50.dp)
                .background(MaterialTheme.colorScheme.surface, shape)
                .platformFocus()
        ) {
        }

        content()
    }
}
