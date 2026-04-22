package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.PlatformFileChooser
import de.visualdigits.common.presentation.components.PlatformFileSaver
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.dialog_title_export_opml
import de.visualdigits.compose.resources.dialog_title_import_opml
import de.visualdigits.compose.resources.label_export_opml
import de.visualdigits.compose.resources.label_import_opml
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.stringResource
import java.io.File
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Composable
fun SettingsPage(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onAction: (NewsHomeReaderAction) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.title_settings),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsMenuBar(
            onAction = onAction,
            state = state
        )

        Spacer(Modifier.height(16.dp))

        ConfigurationEditForm(
            scrollPosition = scrollPosition,
            scrollbarId = "configuration_settings",
            fieldHeight = 50.dp,
            onValueChange = { keyValue ->
                onAction(
                    NewsHomeReaderAction.OnSettingsValueChanged(
                        settings = state.settings,
                        keyValue = keyValue
                    )
                )
            },
            configuration = state.settings,
            onCancelClick = {
                onAction(
                    NewsHomeReaderAction.OnEditSettingsCancelClick()
                )
            },
            onOkClick = {
                onAction(
                    NewsHomeReaderAction.OnSaveSettingsClick(
                        settings = state.settings ?: error("No Settings")
                    )
                )
            },
            onAction = onAction
        )
    }
}
