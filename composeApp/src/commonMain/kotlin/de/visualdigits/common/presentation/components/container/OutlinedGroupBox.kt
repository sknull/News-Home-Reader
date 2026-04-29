package de.visualdigits.common.presentation.components.container

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.color
import de.visualdigits.common.presentation.components.PlatformToolTip


@Composable
fun OutlinedGroupBox(
    modifier: Modifier = Modifier,
    label: String,
    toolTip: String? = null,
    space: Dp,
    toolTipBackgroundColor: Color,
    toolTipShape: Shape,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    buttonShape: Shape,
    valid: () -> Boolean? = { true },
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    PlatformToolTip(
        text = toolTip,
        space = space,
        shape = toolTipShape,
        backgroundColor = toolTipBackgroundColor
    ) {
        BasicTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = space),
            value = "",
            onValueChange = { },
            readOnly = true,
            singleLine = false,
            interactionSource = interactionSource,
            decorationBox = { _ ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = "",
                    innerTextField = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            content()
                        }
                    },
                    visualTransformation = VisualTransformation.None,
                    label = { Text(label) },
                    singleLine = false,
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (valid() == true) unfocusedBorderColor else Severity.Error.color(),
                        focusedBorderColor = focusedBorderColor,
                    ),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (valid() == true) unfocusedBorderColor else Severity.Error.color(),
                                focusedBorderColor = focusedBorderColor,
                            ),
                            shape = buttonShape,
                        )
                    },
                )
            },
        )
    }
}
