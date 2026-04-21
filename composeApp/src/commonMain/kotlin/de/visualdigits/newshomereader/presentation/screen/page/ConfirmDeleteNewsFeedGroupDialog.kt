package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.cancel
import de.visualdigits.compose.resources.icon_warning_24px
import de.visualdigits.compose.resources.ok
import de.visualdigits.compose.resources.title_delete
import de.visualdigits.compose.resources.warning_delete
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ConfirmDeleteNewsFeedGroupDialog(
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    if (state.isDeletingNewsFeedGroup || state.isDeletingNewsFeedConfiguration) {
        AlertDialog(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small),
            containerColor = MaterialTheme.colorScheme.error,
            textContentColor = MaterialTheme.colorScheme.onError,
            shape = MaterialTheme.shapes.small,
            onDismissRequest = {},
            title = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(Res.string.title_delete),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(100.dp)
                            .aspectRatio(1.0f),
                        painter = painterResource(Res.drawable.icon_warning_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onError
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(Res.string.warning_delete),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            confirmButton = {
                IndicatorButton(
                    modifier = Modifier,
                    text = stringResource(Res.string.ok),
                    textColor = MaterialTheme.colorScheme.error,
                    buttonColor = MaterialTheme.colorScheme.onError
                ) {
                    if (state.isDeletingNewsFeedConfiguration) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsFeedConfigurationOkClick())
                    } else if (state.isDeletingNewsFeedGroup) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsfeedGroupOkClick())
                    }
                }
            },
            dismissButton = {
                IndicatorButton(
                    modifier = Modifier,
                    text = stringResource(Res.string.cancel),
                    textColor = MaterialTheme.colorScheme.error,
                    buttonColor = MaterialTheme.colorScheme.onError
                ) {
                    if (state.isDeletingNewsFeedConfiguration) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsFeedConfigurationCancelClick())
                    } else if (state.isDeletingNewsFeedGroup) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsfeedGroupCancelClick())
                    }
                }
            }
        )
    }
}
