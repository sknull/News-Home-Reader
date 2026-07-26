package de.visualdigits.newshomereader.presentation.page.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.color.HsvColor
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.domain.model.ui.FileMode
import de.visualdigits.common.presentation.components.PlatformFileChooser
import de.visualdigits.common.presentation.components.PlatformFileSaver
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.dialog_title_export_opml
import de.visualdigits.compose.resources.dialog_title_export_settings
import de.visualdigits.compose.resources.dialog_title_import_opml
import de.visualdigits.compose.resources.dialog_title_import_settings
import de.visualdigits.compose.resources.icon_download_24px
import de.visualdigits.compose.resources.icon_upload_24px
import de.visualdigits.compose.resources.label_opml
import de.visualdigits.compose.resources.label_settings
import de.visualdigits.compose.resources.save
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import kotlinx.io.files.Path
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun SettingsMenuBar(
    viewModel: NewsHomeReaderViewModel,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val buttonColor = remember { (settings?.get<HsvColor>(SK.buttonColor) ?: BUTTON_COLOR_DEFAULT).toComposeColor() }

    val homeDirectoryPath: String = koinInject(qualifier = named("homeDirectory"))

    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
    ) {
        PlatformFileChooser(
            label = stringResource(Res.string.label_opml),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            buttonTextAlign = TextAlign.Start,
            title = stringResource(Res.string.dialog_title_import_opml),
            fileMode = FileMode.FILES_ONLY,
            buttonColor = buttonColor,
            leadingIcon = painterResource(Res.drawable.icon_download_24px),
            startDirectory = Path(homeDirectoryPath, "backup"),
            onOkSource = { fileName, source ->
                onAction(NewsHomeReaderAction.OnOpmlImport(fileName, source))
            }
        )

        PlatformFileSaver(
            label = stringResource(Res.string.label_opml),
            labelSaveButton = stringResource(Res.string.save),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            buttonTextAlign = TextAlign.Start,
            title = stringResource(Res.string.dialog_title_export_opml),
            fileMode = FileMode.FILES_ONLY,
            suggestedFileName = "nhr-export_${
                KmpOffsetDateTime.now().format("yyyy-MM-dd_HH-mm-ss")
            }.opml",
            buttonColor = buttonColor,
            leadingIcon = painterResource(Res.drawable.icon_upload_24px),
            startDirectory = Path(homeDirectoryPath, "backup"),
        ) { fileName, sink ->
            onAction(NewsHomeReaderAction.OnOpmlExport(fileName, sink))
        }

        PlatformFileChooser(
            label = stringResource(Res.string.label_settings),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            buttonTextAlign = TextAlign.Start,
            title = stringResource(Res.string.dialog_title_import_settings),
            fileMode = FileMode.FILES_ONLY,
            buttonColor = buttonColor,
            leadingIcon = painterResource(Res.drawable.icon_download_24px),
            startDirectory = Path(homeDirectoryPath, "backup"),
            onOkSource = { fileName, source ->
                onAction(NewsHomeReaderAction.OnSettingsImport(fileName, source))
            }
        )

        PlatformFileSaver(
            label = stringResource(Res.string.label_settings),
            labelSaveButton = stringResource(Res.string.save),
            buttonTextStyle = MaterialTheme.typography.bodySmall,
            buttonTextAlign = TextAlign.Start,
            title = stringResource(Res.string.dialog_title_export_settings),
            fileMode = FileMode.FILES_ONLY,
            suggestedFileName = "nhr-settings_${
                KmpOffsetDateTime.now().format("yyyy-MM-dd_HH-mm-ss")
            }.json",
            buttonColor = buttonColor,
            leadingIcon = painterResource(Res.drawable.icon_upload_24px),
            startDirectory = Path(homeDirectoryPath, "backup"),
        ) { fileName, sink ->
            onAction(NewsHomeReaderAction.OnSettingsExport(fileName, sink))
        }
    }
}
