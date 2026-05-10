package de.visualdigits.newshomereader.presentation.page.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.form.EditableListResources
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.add
import de.visualdigits.compose.resources.add_hint
import de.visualdigits.compose.resources.cancel
import de.visualdigits.compose.resources.choose_directory
import de.visualdigits.compose.resources.choose_file
import de.visualdigits.compose.resources.delete
import de.visualdigits.compose.resources.edit
import de.visualdigits.compose.resources.icon_add_24px
import de.visualdigits.compose.resources.icon_cancel_24px
import de.visualdigits.compose.resources.icon_check_small_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.compose.resources.icon_file_save_24px
import de.visualdigits.compose.resources.icon_folder_open_24px
import de.visualdigits.compose.resources.ok
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun SettingsPage(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onCommonAction: (CommonAction) -> Unit,
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

        SettingsMenuBar(onAction = onAction)

        Spacer(Modifier.height(16.dp))

        ConfigurationEditForm(
            titleChooseDirectory = UiText.StringResourceId(Res.string.choose_directory),
            titleChooseFile = UiText.StringResourceId(Res.string.choose_file),
            iconFolder = painterResource(Res.drawable.icon_folder_open_24px),
            editableListResources = EditableListResources(
                hintAdd = UiText.StringResourceId(Res.string.add_hint),
                titleAdd = UiText.StringResourceId(Res.string.add),
                iconAdd = painterResource(Res.drawable.icon_add_24px),
                titleEdit = UiText.StringResourceId(Res.string.edit),
                iconEdit = painterResource(Res.drawable.icon_edit_24px),
                toolTipDelete = UiText.StringResourceId(Res.string.delete),
                iconDelete = painterResource(Res.drawable.icon_delete_24px),
                toolTipEdit = UiText.StringResourceId(Res.string.edit),
                labelOk = UiText.StringResourceId(Res.string.ok),
                labelCancel = UiText.StringResourceId(Res.string.cancel),
                iconCancel = painterResource(Res.drawable.icon_cancel_24px),
                iconSaveFile = painterResource(Res.drawable.icon_file_save_24px)
            ),
            tooltipOk = UiText.StringResourceId(Res.string.ok),
            iconOk = painterResource(Res.drawable.icon_check_small_24px),
            tooltipCancel = UiText.StringResourceId(Res.string.cancel),
            iconCancel = painterResource(Res.drawable.icon_cancel_24px),
            scrollPosition = scrollPosition,
            scrollbarId = "configuration_settings",
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(10.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
            scrollbarStyle = scrollbarStyle(),
            fieldHeight = 50.dp,
            onValueChange = { keyValue ->
                onAction(
                    NewsHomeReaderAction.OnSettingsValueChanged(
                        keyValue = keyValue
                    )
                )
            },
            configuration = { state.settings!! },
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
            onCommonAction = onCommonAction
        )
    }
}
