package de.visualdigits.common.presentation.components.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.util.minimizedLabelHalfHeight
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.stringResource

@Composable
fun SwitchBox(
    field: Field<*, *, *>,
    enabled: Boolean = true,
    fieldHeight: Dp,
    finalUnfocusedBorderColor: Color,
    focusedBorderColor: Color,
    buttonShape: Shape,
    textStyle: TextStyle,
    onValueChange: (KeyValue) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val booleanValue = when (val v = field.value) {
        is BooleanEnum -> v.booleanValue
        is Boolean -> v
        is String -> v.toBoolean()
        else -> false
    }
    var checked by remember {
        mutableStateOf(booleanValue)
    }
    val textFieldState = rememberTextFieldState(" ")
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight + minimizedLabelHalfHeight(textStyle)),
            textStyle = textStyle,
            label = {
                Text(
                    text = stringResource(field.descriptor.label),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            enabled = enabled,
            shape = buttonShape,
            readOnly = true,
            state = textFieldState,
            leadingIcon = {
                Row {
                    Spacer(Modifier.width(MaterialTheme.shapes.gap * 2))
                    Switch(
                        checked = checked,
                        onCheckedChange = { v ->
                            checked = v
                            onValueChange(KeyValue(field.descriptor, v.toString()))
                        },
                        interactionSource = interactionSource,
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.onSurface,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            checkedBorderColor = MaterialTheme.colorScheme.onSurface,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = finalUnfocusedBorderColor,
                focusedBorderColor = focusedBorderColor
            )
        )
    }
}
