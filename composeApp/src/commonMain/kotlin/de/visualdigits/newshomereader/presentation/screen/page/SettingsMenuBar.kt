package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.PlatformFileChooser
import de.visualdigits.common.presentation.components.PlatformFileSaver
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.dialog_title_export_opml
import de.visualdigits.compose.resources.dialog_title_import_opml
import de.visualdigits.compose.resources.label_export_opml
import de.visualdigits.compose.resources.label_import_opml
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.stringResource
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsMenuBar(
    onAction: (NewsHomeReaderAction) -> Unit,
    state: NewsHomeReaderState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
    ) {
        PlatformFileChooser(
            label = stringResource(Res.string.label_import_opml),
            title = stringResource(Res.string.dialog_title_import_opml),
            buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            fileMode = FileMode.FILES_ONLY,
        ) { ins ->
            onAction(NewsHomeReaderAction.OnOpmlImport(ins))
        }

        PlatformFileSaver(
            label = stringResource(Res.string.label_export_opml),
            title = stringResource(Res.string.dialog_title_export_opml),
            buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            fileMode = FileMode.FILES_ONLY,
            suggestedFileName = "newshomereader-export_${
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
            }.opml",
        ) { outs ->
            onAction(NewsHomeReaderAction.OnOpmlExport(outs))
        }
    }
}
