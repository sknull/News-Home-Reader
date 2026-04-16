package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.button.IndicatorButton
import java.io.File
import java.io.InputStream
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


@Composable
actual fun PlatformFileChooser(
    label: String,
    title: String,
    fileMode: FileMode,
    startDirectory: File?,
    options: List<String>,
    buttonShape: Shape,
    buttonColor: Color,
    onCancel: (() -> Unit)?,
    onOk: (InputStream) -> Unit
) {
    val mode = fileMode.jFileChooserMode
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
        width = 180.dp,
        text = label,
        buttonColor = buttonColor,
        shape = buttonShape,
    ) {
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            onOk(chooser.selectedFile.inputStream())
        } else if (result == JFileChooser.CANCEL_OPTION) {
            onCancel?.also { oc -> oc() }
        }
    }
}
