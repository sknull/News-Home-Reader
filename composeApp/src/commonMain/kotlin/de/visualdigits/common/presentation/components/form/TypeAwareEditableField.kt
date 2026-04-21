package de.visualdigits.common.presentation.components.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.Enumerable
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.color
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.FileFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ReferenceListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.PlatformToolTip
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.util.minimizedLabelHalfHeight
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.choose_directory
import de.visualdigits.compose.resources.choose_file
import de.visualdigits.compose.resources.icon_folder_open_24px
import de.visualdigits.newshomereader.presentation.style.gap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun TypeAwareEditableField(
    modifier: Modifier = Modifier,
    configuration: AbstractConfiguration<*,*>?,
    field: Field<*,*,*>,
    currentValue: String? = null,
    fieldHeight: Dp = Dp.Unspecified,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
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
                    field = field,
                    fieldHeight = fieldHeight,
                    finalUnfocusedBorderColor = finalUnfocusedBorderColor,
                    focusedBorderColor = focusedBorderColor,
                    buttonShape = buttonShape,
                    textStyle = textStyle,
                    onValueChange = onValueChange
                )
            } else {
                ComboBox(
                    modifier = modifier
                        .focusRequester(focusRequester),
                    textStyle = textStyle,
                    field = field,
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
            val titleDirectories = stringResource((Res.string.choose_directory))
            val titleFiles = stringResource((Res.string.choose_file))

            PlatformToolTip(field.descriptor.toolTip?.let { t -> stringResource(t) }, content = {
                OutlinedTextField(
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .height(fieldHeight),
                    textStyle = textStyle,
                    enabled = enabled,
                    value = value ?: "",
                    label = {
                        Text(
                            text = stringResource(field.descriptor.label),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = {
                        trailingIcon?.let { ti -> ti() }

                        if (enabled) {
                            IndicatorButton(
                                leadingIcon = painterResource(Res.drawable.icon_folder_open_24px),
                                leadingIconTint = iconTint,
                                modifier = Modifier.padding(start = 5.dp),
                                shape = buttonShape,
                                buttonColor = buttonColor,
                                onClick = {
                                    val fileMode = field.descriptor.fileMode
                                    scope.launch(Dispatchers.IO) {
                                        desktopFileChooser(
                                            title = when (fileMode) {
                                                FileMode.DIRECTORIES_ONLY -> titleDirectories
                                                FileMode.FILES_ONLY -> titleFiles
                                            },
                                            fileMode = fileMode,
                                            options = field.descriptor.options(),
                                            startDirectory = (field.value as? File) ?: field.descriptor.startDirectory(configuration)
                                        ){ file ->
                                            onValueChange(
                                                KeyValue(
                                                    field.descriptor,
                                                    file.canonicalPath
                                                )
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    },
                    shape = buttonShape,
                    onValueChange = { value ->
                        onValueChange(KeyValue(field.descriptor, value))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = finalUnfocusedBorderColor,
                        focusedBorderColor = focusedBorderColor
                    )
                )
            })
        }

        else -> {
            val label = stringResource(field.descriptor.label)
            PlatformToolTip(field.descriptor.toolTip?.let { t -> stringResource(t) }, content = {
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
                    onValueChange = { value ->
                        onValueChange(KeyValue(field.descriptor, value))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = finalUnfocusedBorderColor,
                        focusedBorderColor = focusedBorderColor,
                        cursorColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            })
        }
    }

    if (hasFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun SwitchBox(
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
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            checkedBorderColor = MaterialTheme.colorScheme.primaryContainer,
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

private fun desktopFileChooser(
    title: String,
    fileMode: FileMode,
    startDirectory: File,
    options: List<Triple<String, UiText?, DrawableResource?>>,
    onOk: (File) -> Unit
) {
    val mode = fileMode.jFileChooserMode
    val chooser = JFileChooser().apply {
        if (fileMode == FileMode.FILES_ONLY) {
            val filter =
                FileNameExtensionFilter(
                    options.map { o -> o.first }
                        .joinToString(", ") { o -> "*.$o" },
                    *options.map { o -> o.first }.toTypedArray()
                )
            this.fileFilter = filter
            this.isAcceptAllFileFilterUsed = false
        }
        currentDirectory = startDirectory
        fileSelectionMode = mode
        dialogTitle = title
    }
    val result = chooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        onOk(chooser.selectedFile)
    } else {
        // nothing to do
    }
}
