package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.button.IndicatorButton
import java.io.InputStream
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


@Composable
actual fun PlatformFileChooser(
    label: String,
    buttonTextStyle: TextStyle,
    title: String,
    fileMode: FileMode,
    options: List<String>,
    buttonShape: Shape,
    buttonColor: Color,
    buttonWidth: Dp,
    buttonHeight: Dp,
    onCancel: (() -> Unit)?,
    onOk: (String, InputStream) -> Unit
) {
    val mode = fileMode.jFileChooserMode
    val startDirectory = Paths.get(System.getProperty("user.home"), ".newshomereader", "backup").toFile()
    val chooser = JFileChooser().apply {
        if (fileMode == FileMode.FILES_ONLY && options.isNotEmpty()) {
            val filter =
                FileNameExtensionFilter(
                    options
                        .joinToString(", ") { o -> "*.$o" },
                    *options.toTypedArray()
                )
            this.fileFilter = filter
            this.isAcceptAllFileFilterUsed = false
        } else {
            this.isAcceptAllFileFilterUsed = true
        }
        currentDirectory = startDirectory
        fileSelectionMode = mode
        dialogTitle = title
    }

    IndicatorButton(
        modifier = Modifier,
        width = buttonWidth,
        height = buttonHeight,
        text = label,
        textStyle = buttonTextStyle,
        buttonColor = buttonColor,
        shape = buttonShape,
    ) {
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            onOk(chooser.selectedFile.name, chooser.selectedFile.inputStream())
        } else if (result == JFileChooser.CANCEL_OPTION) {
            onCancel?.also { oc -> oc() }
        }
    }
}
