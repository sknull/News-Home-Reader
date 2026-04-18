package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.presentation.components.PlatformFileChooser
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.dialog_title_import_opml
import de.visualdigits.compose.resources.label_import_opml
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import org.jetbrains.compose.resources.stringResource


@Composable
fun SettingsPage(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Int>,
    onAction: (NewsHomeReaderAction) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {

        ErrorCard(
            errorMessage = state.uiMessage,
            severity = state.uiMessageSeverity,
            shapeContainer = MaterialTheme.shapes.small
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            PlatformFileChooser(
                label = stringResource(Res.string.label_import_opml),
                title = stringResource(Res.string.dialog_title_import_opml),
                buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                fileMode = FileMode.FILES_ONLY,
            ) { ins ->
                onAction(NewsHomeReaderAction.OnOpmlImport(ins))
            }

            if (state.currentProgress > 0.0f) {
                Spacer(Modifier.weight(1f))

                val animatedProgress by animateFloatAsState(
                    targetValue = state.currentProgress,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec // Sorgt für sanftes Gleiten
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                    trackColor = MaterialTheme.colorScheme.surfaceDim,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        ConfigurationEditForm(
            scrollPosition = scrollPosition,
            title = stringResource(Res.string.title_settings),
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
