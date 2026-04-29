package de.visualdigits.common.presentation.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.presentation.components.PlatformToolTip
import de.visualdigits.common.presentation.components.button.IndicatorButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import java.io.File

@Composable
fun FileChooserBox(
    modifier: Modifier,
    iconFolder: Painter,
    space: Dp,
    toolTipBackgroundColor: Color,
    toolTipShape: Shape,
    toolTip: String?,
    focusRequester: FocusRequester,
    fieldHeight: Dp,
    textStyle: TextStyle,
    enabled: Boolean,
    value: String?,
    label: String,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    iconTint: Color,
    buttonShape: Shape,
    buttonColor: Color,
    scope: CoroutineScope,
    fileMode: FileMode,
    titleDirectories: String,
    titleFiles: String,
    options: List<Triple<String, UiText?, DrawableResource?>>,
    startDirectory: File,
    finalUnfocusedBorderColor: Color,
    focusedBorderColor: Color,
    onValueChange: (String) -> Unit,
    onOk: (File) -> Unit
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
                .height(fieldHeight),
            textStyle = textStyle,
            enabled = enabled,
            value = value ?: "",
            label = {
                Text(
                    text = label,
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
                        leadingIcon = iconFolder,
                        leadingIconTint = iconTint,
                        modifier = Modifier.padding(start = 5.dp),
                        shape = buttonShape,
                        buttonColor = buttonColor,
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                desktopFileChooser(
                                    title = when (fileMode) {
                                        FileMode.DIRECTORIES_ONLY -> titleDirectories
                                        FileMode.FILES_ONLY -> titleFiles
                                    },
                                    fileMode = fileMode,
                                    options = options,
                                    startDirectory = startDirectory,
                                    onOk = onOk
                                )
                            }
                        }
                    )
                }
            },
            shape = buttonShape,
            onValueChange = onValueChange,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = finalUnfocusedBorderColor,
                focusedBorderColor = focusedBorderColor
            )
        )
    }
}
