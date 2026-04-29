package de.visualdigits.common.presentation.components.container

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun VerticalCollapsibleBox(
    modifier: Modifier = Modifier,
    iconArrowRight: Painter,
    iconArrowDown: Painter,
    space: Dp = 8.dp,
    title: String?,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.small,
    onStateChange: (Boolean) -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    isExpanded: Boolean,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (androidPlatform() == UiPlatform.UI_MODE_TYPE_TELEVISION) {
        VerticalCollapsibleBoxTv(
            modifier = modifier,
            space = space,
            title = title,
            backgroundColor = backgroundColor,
            shape = shape,
            trailingIcon = trailingIcon,
            content = content
        )
    } else {
        VerticalCollapsibleBoxFull(
            modifier = modifier,
            iconArrowRight = iconArrowRight,
            iconArrowDown = iconArrowDown,
            space = space,
            title = title,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedBorderColor = focusedBorderColor,
            backgroundColor = backgroundColor,
            shape = shape,
            iconTint = iconTint,
            onStateChange = onStateChange,
            isExpanded = isExpanded,
            trailingIcon = trailingIcon,
            content = content
        )
    }
}

@Composable
fun VerticalCollapsibleBoxFull(
    modifier: Modifier = Modifier,
    iconArrowRight: Painter,
    iconArrowDown: Painter,
    space: Dp = 9.dp,
    title: String?,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    backgroundColor: Color,
    shape: Shape,
    iconTint: Color = Color.White,
    onStateChange: (Boolean) -> Unit,
    isExpanded: Boolean,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 0
                )
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioNoBouncy,
//                    stiffness = Spring.StiffnessLow
//                )
            ),
        value = "",
        onValueChange = { },
        readOnly = true,
        singleLine = false,
        decorationBox = { _ ->
            OutlinedTextFieldDefaults.DecorationBox(
                innerTextField = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(space),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable {
                                    onStateChange(!isExpanded)
                                },
                            horizontalArrangement = Arrangement.spacedBy(space),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            title?.let { t ->
                                Text(
                                    modifier = Modifier
                                        .padding(space),
                                    text = t,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (isExpanded) {
                                Icon(
                                    modifier = Modifier
                                        .padding(space),
                                    painter = iconArrowDown,
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .padding(space),
                                    painter = iconArrowRight,
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            }

                            trailingIcon?.let { ti -> ti() }
                        }

                        if (isExpanded) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = space),
                            ) {
                                content()
                            }
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
                contentPadding = PaddingValues(0.dp)
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
fun VerticalCollapsibleBoxTv(
    modifier: Modifier = Modifier,
    space: Dp = 8.dp,
    title: String?,
    backgroundColor: Color,
    shape: Shape,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(backgroundColor, shape)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(space)
    ) {
        title?.let { t ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(space)
                    .background(backgroundColor.copy(alpha = 0.4f), shape)
                    .platformFocus(),
                horizontalArrangement = Arrangement.spacedBy(space)
            ) {
                Text(
                    modifier = Modifier
                        .padding(space),
                    text = t,
                    style = MaterialTheme.typography.titleSmall
                )

                trailingIcon?.let { ti -> ti() }
            }
        }

        content()
    }}
