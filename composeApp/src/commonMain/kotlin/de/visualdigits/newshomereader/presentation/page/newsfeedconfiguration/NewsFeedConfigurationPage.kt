package de.visualdigits.newshomereader.presentation.page.newsfeedconfiguration

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import de.visualdigits.compose.resources.title_add_newsfeedconfiguration
import de.visualdigits.compose.resources.title_edit_newsfeedconfiguration
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewsFeedConfigurationPage(
    state: NewsHomeReaderState,
    viewModel: NewsHomeReaderViewModel,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = if (state.isEditingNewsFeedConfiguration) stringResource(Res.string.title_edit_newsfeedconfiguration) else stringResource(
                Res.string.title_add_newsfeedconfiguration
            ),
            style = MaterialTheme.typography.headlineMedium
        )

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
            scrollPosition = viewModel.scrollPosition,
            scrollbarId = "configuration_settings",
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(10.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
            scrollbarStyle = scrollbarStyle(),
            fieldHeight = 50.dp,
            onValueChange = { keyValue ->
                onAction(
                    NewsHomeReaderAction.OnNewsFeedConfigurationValueChanged(
                        newsFeedConfiguration = state.editedNewsFeedConfiguration,
                        keyValue = keyValue
                    )
                )
            },
            configuration = { state.editedNewsFeedConfiguration!! },
            onCancelClick = {
                onAction(
                    if (state.isAddingNewsFeedConfiguration) {
                        NewsHomeReaderAction.OnAddNewsFeedConfigurationCancelClick()
                    } else {
                        NewsHomeReaderAction.OnEditNewsFeedConfigurationCancelClick()
                    }
                )
            },
            onOkClick = {
                onAction(
                    if (state.isAddingNewsFeedConfiguration) {
                        NewsHomeReaderAction.OnAddNewsFeedConfigurationOkClick(state.editedNewsFeedConfiguration)
                    } else {
                        NewsHomeReaderAction.OnEditNewsFeedConfigurationOkClick(state.editedNewsFeedConfiguration)
                    }
                )
            },
            onCommonAction = onCommonAction,
        )
    }
}
