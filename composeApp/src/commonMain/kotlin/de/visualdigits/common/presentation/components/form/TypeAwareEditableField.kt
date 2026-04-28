package de.visualdigits.common.presentation.components.form

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.Enumerable
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.common.domain.model.color
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.FieldKey
import de.visualdigits.common.domain.model.configuration.FileFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ReferenceListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.choose_directory
import de.visualdigits.compose.resources.choose_file
import org.jetbrains.compose.resources.stringResource
import java.io.File

@Composable
fun TypeAwareEditableField(
    modifier: Modifier = Modifier,
    configuration: AbstractConfiguration<*,*>,
    fieldKey: FieldKey<*>,
    currentValue: String? = null,
    fieldHeight: Dp = Dp.Unspecified,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    buttonShape: Shape = MaterialTheme.shapes.extraSmall,
    buttonColor: Color = MaterialTheme.colorScheme.surface,
    enabled: Boolean = true,
    onValueChange: (KeyValue) -> Unit,
    hasFocus: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val field = configuration.fields[fieldKey]?:error("No field with key '$fieldKey'")
    val value = currentValue?:field.stringValue()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val valid = field.valid(field.value)
    val finalUnfocusedBorderColor = if (!valid) {
        Severity.Error.color()
    } else if (value == null) {
        Severity.Warn.color()
    } else {
        unfocusedBorderColor
    }

    when {
        field.descriptor is EnumFieldDescriptor
                || field.descriptor is ReferenceListFieldDescriptor
                || field.descriptor.itemClass?.java?.let { fc -> Enumerable::class.java.isAssignableFrom(fc) } == true -> {
            if (field.descriptor.fieldClass == BooleanEnum::class) {
                SwitchBox(
                    label = stringResource(field.descriptor.label),
                    value = field.value,
                    fieldHeight = fieldHeight,
                    focusedBorderColor = focusedBorderColor,
                    unfocusedBorderColor = finalUnfocusedBorderColor,
                    buttonShape = buttonShape,
                    textStyle = textStyle
                ) { v ->
                    onValueChange(KeyValue(field.descriptor, v.toString()))
                }
            } else {
                ComboBox(
                    modifier = modifier
                        .focusRequester(focusRequester),
                    textStyle = textStyle,
                    configuration = configuration,
                    fieldKey = fieldKey,
                    fieldHeight = fieldHeight,
                    enabled = enabled,
                    focusedBorderColor = focusedBorderColor,
                    unfocusedBorderColor = finalUnfocusedBorderColor,
                    buttonShape = buttonShape,
                    onValueChange = onValueChange,
                )
            }
        }

        field.descriptor is FileFieldDescriptor -> {
            FileChooserBox(
                toolTip = field.descriptor.toolTip?.let { t -> stringResource(t) },
                modifier = modifier,
                focusRequester = focusRequester,
                fieldHeight = fieldHeight,
                textStyle = textStyle,
                enabled = enabled,
                value = value,
                label = stringResource(field.descriptor.label),
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                iconTint = iconTint,
                buttonShape = buttonShape,
                buttonColor = buttonColor,
                scope = scope,
                fileMode = field.descriptor.fileMode,
                titleDirectories = stringResource((Res.string.choose_directory)),
                titleFiles = stringResource((Res.string.choose_file)),
                options = field.descriptor.options(configuration),
                startDirectory = (field.value as? File) ?: field.descriptor.startDirectory(configuration),
                onOk = { file: File ->
                    onValueChange(
                        KeyValue(
                            field.descriptor,
                            file.canonicalPath
                        )
                    )
                },
                onValueChange = { value: String ->
                    onValueChange(KeyValue(field.descriptor, value))
                },
                finalUnfocusedBorderColor = finalUnfocusedBorderColor,
                focusedBorderColor = focusedBorderColor
            )
        }

        else -> {
            TextBox(
                toolTip = field.descriptor.toolTip?.let { t -> stringResource(t) },
                modifier = modifier,
                focusRequester = focusRequester,
                fieldHeight = fieldHeight,
                textStyle = textStyle,
                enabled = enabled,
                label = stringResource(field.descriptor.label),
                value = value,
                buttonShape = buttonShape,
                onValueChange = { value: String ->
                    onValueChange(KeyValue(field.descriptor, value))
                },
                finalUnfocusedBorderColor = finalUnfocusedBorderColor,
                focusedBorderColor = focusedBorderColor
            )
        }
    }

    if (hasFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
