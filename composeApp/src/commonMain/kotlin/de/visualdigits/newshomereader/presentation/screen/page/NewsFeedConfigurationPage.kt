package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.title_add_newsfeedconfiguration
import de.visualdigits.compose.resources.title_edit_newsfeedconfiguration
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewsFeedConfigurationPage(
    state: NewsHomeReaderState,
    viewModel: NewsHomeReaderViewModel,
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
            scrollPosition = viewModel.scrollPosition,
            scrollbarId = "configuration_settings",
            fieldHeight = 50.dp,
            onValueChange = { keyValue ->
                onAction(
                    NewsHomeReaderAction.OnNewsFeedConfigurationValueChanged(
                        newsFeedConfiguration = state.editedNewsFeedConfiguration,
                        keyValue = keyValue
                    )
                )
            },
            configuration = state.editedNewsFeedConfiguration,
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
            onAction = onAction
        )
    }
}
