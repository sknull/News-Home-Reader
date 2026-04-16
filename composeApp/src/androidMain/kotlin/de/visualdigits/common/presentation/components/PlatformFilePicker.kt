package de.visualdigits.common.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.button.IndicatorButton
import java.io.File
import java.io.InputStream

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
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        try {
            uri?.also { safeUri ->
                context.contentResolver.openInputStream(safeUri)?.use { ins ->
                    val bytes = ins.readBytes()
                    onOk(bytes.inputStream())
                }
            }
        } catch (e: Exception) {
            Logger.e("Could not pick file", e)
        }
    }

    IndicatorButton(
        modifier = Modifier,
        width = 180.dp,
        text = label,
        shape = buttonShape,
        buttonColor = buttonColor
    ) {
        launcher.launch("*/*")
    }
}
