package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.save
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import org.jetbrains.compose.resources.stringResource
import java.io.File
import java.io.OutputStream
import java.nio.file.Paths
import javax.swing.JFileChooser

@Composable
actual fun PlatformFileSaver(
    label: String,
    buttonTextStyle: TextStyle,
    title: String,
    fileMode: FileMode,
    suggestedFileName: String,
    buttonShape: Shape,
    buttonColor: Color,
    buttonWidth: Dp,
    buttonHeight: Dp,
    onCancel: (() -> Unit)?,
    onOk: (String, OutputStream) -> Unit
) {
    val log = kermitLogger("PlatformFileSaver")

    val saveDirectory = Paths.get(System.getProperty("user.home"), ".newshomereader", "backup").toFile()
    if (!saveDirectory.exists()) {
        if (!saveDirectory.mkdirs()) {
            log.e("Failed to create directory ${saveDirectory.absolutePath}")
        }
    }
    val mode = fileMode.jFileChooserMode
    val chooser = JFileChooser().apply {
        isAcceptAllFileFilterUsed = true
        selectedFile = File(saveDirectory, suggestedFileName)
        fileSelectionMode = mode
        dialogTitle = title
        approveButtonText = stringResource(Res.string.save)
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
            onOk(chooser.selectedFile.name, chooser.selectedFile.outputStream())
        } else if (result == JFileChooser.CANCEL_OPTION) {
            onCancel?.also { oc -> oc() }
        }
    }
}
