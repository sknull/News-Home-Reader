package de.visualdigits.newshomereader.presentation.screen.page.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.PlatformFileChooser
import de.visualdigits.common.presentation.components.PlatformFileSaver
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.dialog_title_export_opml
import de.visualdigits.compose.resources.dialog_title_export_settings
import de.visualdigits.compose.resources.dialog_title_import_opml
import de.visualdigits.compose.resources.dialog_title_import_settings
import de.visualdigits.compose.resources.label_export_opml
import de.visualdigits.compose.resources.label_export_settings
import de.visualdigits.compose.resources.label_import_opml
import de.visualdigits.compose.resources.label_import_settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.stringResource
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsMenuBar(
    onAction: (NewsHomeReaderAction) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
    ) {
        PlatformFileChooser(
            label = stringResource(Res.string.label_import_opml),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            title = stringResource(Res.string.dialog_title_import_opml),
            fileMode = FileMode.FILES_ONLY,
            buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) { fileName, ins ->
            onAction(NewsHomeReaderAction.OnOpmlImport(fileName, ins))
        }

        PlatformFileSaver(
            label = stringResource(Res.string.label_export_opml),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            title = stringResource(Res.string.dialog_title_export_opml),
            fileMode = FileMode.FILES_ONLY,
            suggestedFileName = "newshomereader-export_${
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
            }.opml",
            buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) { fileName, outs ->
            onAction(NewsHomeReaderAction.OnOpmlExport(fileName, outs))
        }

        PlatformFileChooser(
            label = stringResource(Res.string.label_import_settings),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            title = stringResource(Res.string.dialog_title_import_settings),
            fileMode = FileMode.FILES_ONLY,
            buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) { fileName, ins ->
            onAction(NewsHomeReaderAction.OnSettingsImport(fileName, ins))
        }

        PlatformFileSaver(
            label = stringResource(Res.string.label_export_settings),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            title = stringResource(Res.string.dialog_title_export_settings),
            fileMode = FileMode.FILES_ONLY,
            suggestedFileName = "newshomereader-settings${
                OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
            }.json",
            buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) { fileName, outs ->
            onAction(NewsHomeReaderAction.OnSettingsExport(fileName, outs))
        }
    }
}
