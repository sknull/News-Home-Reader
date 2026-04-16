package de.visualdigits.common.presentation.components.container

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.color
import de.visualdigits.common.presentation.components.PlatformToolTip
import de.visualdigits.newshomereader.presentation.style.gap


@Composable
fun OutlinedGroupBox(
    label: String,
    toolTip: String? = null,
    modifier: Modifier = Modifier,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    buttonShape: Shape,
    valid: () -> Boolean? = { true },
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    PlatformToolTip(toolTip, content = {
        BasicTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.shapes.gap),
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
                                .fillMaxWidth()
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
                        focusedBorderColor = focusedBorderColor
                    ),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (valid() == true) unfocusedBorderColor else Severity.Error.color(),
                                focusedBorderColor = focusedBorderColor
                            ),
                            shape = buttonShape,
                        )
                    }
                )
            }
        )
    })
}
