package de.visualdigits.common.presentation.components.container

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiPlatform
import de.visualdigits.common.presentation.components.androidPlatform
import de.visualdigits.common.presentation.components.platformFocus
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_arrow_drop_down_24px
import de.visualdigits.compose.resources.icon_arrow_right_24px
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

@Composable
fun VerticalCollapsibleBox(
    modifier: Modifier = Modifier,
    title: String?,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.small,
    containerPadding: Dp = MaterialTheme.shapes.gap,
    onStateChange: (Boolean) -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    isExpanded: Boolean,
    content: @Composable () -> Unit
) {
    if (androidPlatform() == UiPlatform.UI_MODE_TYPE_TELEVISION) {
        VerticalCollapsibleBoxTv(
            modifier = modifier,
            title = title,
            backgroundColor = backgroundColor,
            shape = shape,
            content = content
        )
    } else {
        VerticalCollapsibleBoxFull(
            modifier = modifier,
            title = title,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedBorderColor = focusedBorderColor,
            backgroundColor = backgroundColor,
            shape = shape,
            containerPadding = containerPadding,
            iconTint = iconTint,
            onStateChange = onStateChange,
            isExpanded = isExpanded,
            content = content
        )
    }
}

@Composable
fun VerticalCollapsibleBoxFull(
    modifier: Modifier = Modifier,
    title: String?,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    backgroundColor: Color,
    shape: Shape,
    containerPadding: Dp = MaterialTheme.shapes.gap,
    iconTint: Color = Color.White,
    onStateChange: (Boolean) -> Unit,
    isExpanded: Boolean,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
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
                            .fillMaxWidth()
                            .clip(shape)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable {
                                onStateChange(!isExpanded)
                            },
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            title?.let { t ->
                                Text(
                                    modifier = Modifier
                                        .padding(MaterialTheme.shapes.gap),
                                    text = t,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            if (isExpanded) {
                                Icon(
                                    modifier = Modifier
                                        .padding(MaterialTheme.shapes.gap),
                                    painter = painterResource(Res.drawable.icon_arrow_drop_down_24px),
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .padding(MaterialTheme.shapes.gap),
                                    painter = painterResource(Res.drawable.icon_arrow_right_24px),
                                    contentDescription = null,
                                    tint = iconTint
                                )
                            }
                        }

                        if (isExpanded) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = MaterialTheme.shapes.gap),
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
    title: String?,
    backgroundColor: Color,
    shape: Shape,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(backgroundColor, shape)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        title?.let { t ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.shapes.gap)
                    .background(backgroundColor.copy(alpha = 0.4f), shape)
                    .platformFocus()
            ) {
                Text(
                    modifier = Modifier
                        .padding(MaterialTheme.shapes.gap),
                    text = t,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        content()
    }}
