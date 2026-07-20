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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.form.EditableListResources
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
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
import de.visualdigits.compose.resources.icon_folder_open_24px
import de.visualdigits.compose.resources.icon_visibility_24px
import de.visualdigits.compose.resources.ok
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun SettingsPage(
    viewModel: NewsHomeReaderViewModel,
    platformType: PlatformType,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {

    val editedSettings by viewModel.editedSettings.collectAsState()

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

        ConfigurationEditForm(
            titleChooseDirectory = UiText.StringResourceId(Res.string.choose_directory),
            titleChooseFile = UiText.StringResourceId(Res.string.choose_file),
            iconFolder = painterResource(Res.drawable.icon_folder_open_24px),
            editableListResources = EditableListResources(
                titleAdd = UiText.StringResourceId(Res.string.add),
                titleEdit = UiText.StringResourceId(Res.string.edit),
                tooltipAdd = UiText.StringResourceId(Res.string.add_hint),
                iconAdd = Res.drawable.icon_add_24px,
                toolTipEdit = UiText.StringResourceId(Res.string.edit),
                iconEdit = Res.drawable.icon_edit_24px,
                toolTipDelete = UiText.StringResourceId(Res.string.delete),
                iconDelete = Res.drawable.icon_delete_24px,
                labelOk = UiText.StringResourceId(Res.string.ok),
                iconOk = Res.drawable.icon_check_small_24px,
                labelCancel = UiText.StringResourceId(Res.string.cancel),
                iconCancel = Res.drawable.icon_cancel_24px
            ),
            tooltipOk = UiText.StringResourceId(Res.string.ok),
            visibilityIcon = painterResource(Res.drawable.icon_visibility_24px),
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
            colorPickerUseOnlySliders = false,
            fieldHeight = 50.dp,
            onValueChange = { keyValue ->
                onAction(
                    NewsHomeReaderAction.OnSettingsValueChanged(
                        keyValue = keyValue
                    )
                )
            },
            configuration = editedSettings!!,
            onCancelClick = {
                onAction(
                    NewsHomeReaderAction.OnEditSettingsCancelClick()
                )
            },
            onOkClick = {
                onAction(
                    NewsHomeReaderAction.OnSaveSettingsClick()
                )
            },
            onCommonAction = onCommonAction,
            platformType = platformType
        ) {
            Spacer(Modifier.height(16.dp))

            SettingsMenuBar(viewModel = viewModel, onAction = onAction)

            Spacer(Modifier.height(16.dp))
        }
    }
}
