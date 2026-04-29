package de.visualdigits.common.presentation.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.presentation.components.PlatformToolTip
import de.visualdigits.common.presentation.components.util.minimizedLabelHalfHeight

@Composable
fun TextBox(
    modifier: Modifier,
    space: Dp,
    toolTipBackgroundColor: Color,
    toolTipShape: Shape,
    toolTip: String?,
    focusRequester: FocusRequester,
    fieldHeight: Dp,
    textStyle: TextStyle,
    enabled: Boolean,
    label: String,
    value: String?,
    buttonShape: Shape,
    finalUnfocusedBorderColor: Color,
    focusedBorderColor: Color,
    onValueChange: (String) -> Unit
) {
    PlatformToolTip(
        text = toolTip,
        space = space,
        backgroundColor = toolTipBackgroundColor,
        shape = toolTipShape
    ) {
        OutlinedTextField(
            modifier = modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .height(fieldHeight + minimizedLabelHalfHeight(textStyle)),
            textStyle = textStyle,
            enabled = enabled,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            value = value ?: "",
            shape = buttonShape,
            onValueChange = onValueChange,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = finalUnfocusedBorderColor,
                focusedBorderColor = focusedBorderColor,
                cursorColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
