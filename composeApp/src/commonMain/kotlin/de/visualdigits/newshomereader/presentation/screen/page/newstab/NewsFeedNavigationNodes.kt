package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource


@Composable
fun NewsFeedNavigationNodes(
    state: NewsHomeReaderState,
    isLandscape: Boolean,
    newsFeedGroups: List<NewsFeedGroup>,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    newsFeedGroups.forEach { newsFeedGroup ->
        VerticalCollapsibleBox(
            modifier = Modifier
                .fillMaxWidth(),
            title = newsFeedGroup.name,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RectangleShape,
            containerPadding = 0.dp,
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
                    }
                }
            },
            content = {
                if (isLandscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NewsFeedItems(newsFeedGroup, state, onAction)

                        if (state.isEditMode) {
                            IndicatorButton(
                                modifier = Modifier,
                                width = 30.dp,
                                height = 30.dp,
                                padding = 2.dp,
                                leadingIcon = painterResource(Res.drawable.icon_add_24px)
                            ) {
                                onAction(NewsHomeReaderAction.OnAddNewsFeedConfigurationClick(newsFeedGroup.name))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                    ) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            NewsFeedItems(newsFeedGroup, state, onAction)
                        }

                        if (state.isEditMode) {
                            IndicatorButton(
                                modifier = Modifier,
                                width = 30.dp,
                                height = 30.dp,
                                padding = 2.dp,
                                leadingIcon = painterResource(Res.drawable.icon_add_24px)
                            ) {
                                onAction(NewsHomeReaderAction.OnAddNewsFeedConfigurationClick(newsFeedGroup.name))
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun NewsFeedItems(
    newsFeedGroup: NewsFeedGroup,
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    newsFeedGroup.newsFeeds.forEach { newsFeedConfiguration ->
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IndicatorButton(
                modifier = Modifier,
                width = 200.dp - MaterialTheme.shapes.gap * 2,
                height = 50.dp,
                indicatorPosition = Alignment.CenterStart,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                text = newsFeedConfiguration.name,
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.extraSmall,
                selected = state.currentFeedName == newsFeedConfiguration.name
            ) {
                onAction(
                    NewsHomeReaderAction.OnNewsFeedClicked(
                        feedName = newsFeedConfiguration.name,
                        currentFeedConfiguration = newsFeedConfiguration
                    )
                )
            }

            if (state.isEditMode) {
                IndicatorButton(
                    modifier = Modifier,
                    width = 30.dp,
                    height = 30.dp,
                    padding = 2.dp,
                    leadingIcon = painterResource(Res.drawable.icon_edit_24px)
                ) {
                    onAction(NewsHomeReaderAction.OnEditNewsFeedConfigurationClick(newsFeedConfiguration))
                }

                IndicatorButton(
                    modifier = Modifier,
                    width = 30.dp,
                    height = 30.dp,
                    padding = 2.dp,
                    leadingIcon = painterResource(Res.drawable.icon_delete_24px)
                ) {
                    onAction(NewsHomeReaderAction.OnDeleteNewsFeedConfigurationClick(newsFeedConfiguration))
                }
            }
        }
    }
}
