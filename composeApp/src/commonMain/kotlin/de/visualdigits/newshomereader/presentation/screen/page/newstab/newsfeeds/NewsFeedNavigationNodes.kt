package de.visualdigits.newshomereader.presentation.screen.page.newstab.newsfeeds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_docs_add_on_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource


@Composable
fun NewsFeedNavigationNodes(
    modifier: Modifier = Modifier,
    state: NewsHomeReaderState,
    newsFeedGroups: List<NewsFeedGroup>,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Column() {
        if (state.isEditMode) {
            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_add_notes_24px)
            ) {
                onAction(NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick())
            }
        }

        PlatformVerticalScrollbarBox(
            boxModifier = modifier
                .width(500.dp),
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(10.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
            "newsfeed_navigation",
            scrollPosition = scrollPosition,
            onAction
        ) {
            newsFeedGroups.map { newsFeedGroup ->
                Pair("newsfeed_navigation_${newsFeedGroup.name}", @Composable {
                    NewsFeedGroupBox(newsFeedGroup, onAction, state)
                })
            }
        }
    }
}

@Composable
private fun NewsFeedGroupBox(
    newsFeedGroup: NewsFeedGroup,
    onAction: (NewsHomeReaderAction) -> Unit,
    state: NewsHomeReaderState
) {
    VerticalCollapsibleBox(
        modifier = Modifier
            .fillMaxWidth(),
        title = newsFeedGroup.name,
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.small,
        onStateChange = { state ->
            onAction(NewsHomeReaderAction.OnCollapsibleStateChange("group_${newsFeedGroup.name}", state))
        },
        isExpanded = state.collapsibleState["group_${newsFeedGroup.name}"] == true,
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
                        onAction(NewsHomeReaderAction.OnEditNewsfeedGroupGroupClick(newsFeedGroup.name))
                    }

                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_delete_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsfeedGroupClick(newsFeedGroup.name))
                    }

                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_add_notes_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick(parentNewsFeedGroupName = newsFeedGroup.name))
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
                Pair("newsfeed_navigation_${newsFeedGroup.name}_${subNewsFeedGroup.name}", @Composable {
                    NewsFeedGroupBox(subNewsFeedGroup, onAction, state)
                })
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
                        onAction(NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick(parentNewsFeedGroupName = newsFeedGroup.parentGroupName))
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
