package de.visualdigits.newshomereader.presentation.screen.page.newstab.newsfeeds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.compose.resources.icon_arrow_drop_down_24px
import de.visualdigits.compose.resources.icon_arrow_right_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_docs_add_on_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsFeedGroupBox(
    newsFeedGroup: NewsFeedGroup,
    onAction: (NewsHomeReaderAction) -> Unit,
    state: NewsHomeReaderState
) {
    VerticalCollapsibleBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = MaterialTheme.shapes.gap * 2),
        title = newsFeedGroup.name,
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.small,
        onStateChange = { state ->
            onAction(NewsHomeReaderAction.OnNewsFeedGroupCollapsibleStateChange(newsFeedGroup, state))
        },
        isExpanded = state.collapsibleState["group_${newsFeedGroup.name}"] == true,
        iconArrowRight = painterResource(Res.drawable.icon_arrow_right_24px),
        iconArrowDown = painterResource(Res.drawable.icon_arrow_drop_down_24px),
        trailingIcon = {
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
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        ) {
            newsFeedGroup.subGroups.forEach { subNewsFeedGroup ->
                NewsFeedGroupBox(subNewsFeedGroup, onAction, state)
            }

            NewsFeedItems(newsFeedGroup, state, onAction)

            if (state.isEditMode) {
                Row() {
                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_add_notes_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick(newsFeedGroupName = newsFeedGroup.parentGroupName))
                    }

                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_docs_add_on_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnAddNewsFeedConfigurationClick("${newsFeedGroup.name}${newsFeedGroup.parentGroupName?.let{ pcn -> "_$pcn"}}"))
                    }
                }
            }
        }
    }
}
