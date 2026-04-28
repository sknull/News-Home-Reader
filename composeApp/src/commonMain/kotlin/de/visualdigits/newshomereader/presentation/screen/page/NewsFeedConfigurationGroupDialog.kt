package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.cancel
import de.visualdigits.compose.resources.ok
import de.visualdigits.compose.resources.title_add_newsfeedgroup
import de.visualdigits.compose.resources.title_edit_newsfeedgroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import org.jetbrains.compose.resources.stringResource



@Composable
fun NewsFeedConfigurationGroupDialog(
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    if (state.isAddingNewsFeedGroup || state.isEditingNewsFeedGroup) {
        var currentNewsFeedGroupName by remember { mutableStateOf<String>(state.originalNewsFeedGroup?.name?:"") }
        AlertDialog(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small),
            containerColor = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.small,
            onDismissRequest = {},
            title = {
                Text(
                    text = if (state.isAddingNewsFeedGroup) {
                        stringResource(Res.string.title_add_newsfeedgroup)
                    } else if (state.isEditingNewsFeedGroup) {
                        stringResource(Res.string.title_edit_newsfeedgroup)
                    } else "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .height(70.dp),
                    label = { Text(text = if (state.isAddingNewsFeedGroup) stringResource(Res.string.title_add_newsfeedgroup) else state.originalNewsFeedGroup?.name?:"") },
                    value = currentNewsFeedGroupName,
                    shape = MaterialTheme.shapes.extraSmall,
                    onValueChange = { value -> currentNewsFeedGroupName = value },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            },
            confirmButton = {
                IndicatorButton(
                    modifier = Modifier,
                    text = stringResource(Res.string.ok)
                ) {
                    if (state.isAddingNewsFeedGroup) {
                        onAction(NewsHomeReaderAction.OnAddNewsFeedGroupOkClick(currentNewsFeedGroupName))
                    } else if (state.isEditingNewsFeedGroup) {
                        onAction(NewsHomeReaderAction.OnEditNewsFeedGroupOkClick(state.originalNewsFeedGroup, currentNewsFeedGroupName))
                    }
                }
            },
            dismissButton = {
                IndicatorButton(
                    modifier = Modifier,
                    text = stringResource(Res.string.cancel)
                ) {
                    if (state.isAddingNewsFeedGroup) {
                        onAction(NewsHomeReaderAction.OnAddNewsFeedGroupCancelClick())
                    } else if (state.isEditingNewsFeedGroup) {
                        onAction(NewsHomeReaderAction.OnEditNewsFeedGroupCancelClick())
                    }
                }
            }
        )
    }
}
