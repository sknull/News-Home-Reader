package de.visualdigits.newshomereader.presentation.page.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditButtonsTop(
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit,
    newsFeedGroup: NewsFeedGroup
) {
    if (state.isEditMode) {
        Row() {
            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_edit_24px)
            ) {
                onAction(NewsHomeReaderAction.OnEditNewsfeedGroupGroupClick(newsFeedGroup))
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_delete_24px)
            ) {
                onAction(NewsHomeReaderAction.OnDeleteNewsfeedGroupClick(newsFeedGroup))
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_add_notes_24px)
            ) {
                onAction(NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick(newsFeedGroupName = newsFeedGroup.name))
            }
        }
    }
}
