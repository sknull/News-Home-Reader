package de.visualdigits.common.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import java.io.File
import java.io.InputStream

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
    val log = kermitLogger("PlatformFileChooser")

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        try {
            uri?.also { safeUri ->
                var fileName = "unknown"
                context.contentResolver.query(safeUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex)
                    }
                    if (fileName == "unknown") {
                        fileName = safeUri.path?.substringAfterLast('/') ?: "file"
                    }                }

                context.contentResolver.openInputStream(safeUri)?.use { ins ->
                    val bytes = ins.readBytes()
                    onOk(fileName, bytes.inputStream())
                }
            }
        } catch (e: Exception) {
            log.e("Could not pick file", e)
        }
    }

    IndicatorButton(
        modifier = Modifier,
        width = buttonWidth,
        height = buttonHeight,
        text = label,
        textStyle = buttonTextStyle,
        shape = buttonShape,
        buttonColor = buttonColor
    ) {
        launcher.launch("*/*")
    }
}
