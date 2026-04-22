package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import org.jetbrains.compose.resources.stringResource


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
            onAction = onAction
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
