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
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import java.io.OutputStream

@Composable
actual fun PlatformFileSaver(
    label: String,
    title: String,
    fileMode: FileMode,
    suggestedFileName: String,
    buttonShape: Shape,
    buttonColor: Color,
    onCancel: (() -> Unit)?,
    onOk: (OutputStream) -> Unit
) {
    val context = LocalContext.current
    val log = kermitLogger("PlatformFileSaver")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val outs = context.contentResolver.openOutputStream(uri)
                if (outs != null) {
                    onOk(outs)
                }
            } catch (e: Exception) {
                log.e("Could not save file", e)
            }
        } else {
            onCancel?.invoke()
        }
    }

    IndicatorButton(
        modifier = Modifier,
        width = 180.dp,
        text = label,
        shape = buttonShape,
        buttonColor = buttonColor
    ) {
        launcher.launch(suggestedFileName)
    }
}
