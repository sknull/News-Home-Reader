package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.gap
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun NewsFeedNavigationNodes(
    isLandscape: Boolean,
    newsFeedGroups: List<NewsFeedGroup>,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

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
            content = {
                if (isLandscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NewsFeedItems(newsFeedGroup, state, onAction)
                    }
                } else {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        NewsFeedItems(newsFeedGroup, state, onAction)
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
        IndicatorButton(
            modifier = Modifier,
            width = 200.dp - MaterialTheme.shapes.gap * 2,
            height = 50.dp,
            indicatorPosition = Alignment.CenterStart,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            text = newsFeedConfiguration.name,
            textStyle = MaterialTheme.typography.bodySmall,
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
    }
}
