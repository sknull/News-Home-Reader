package de.visualdigits.common.presentation.components.form

import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.domain.model.UiText
import org.jetbrains.compose.resources.DrawableResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


fun desktopFileChooser(
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
