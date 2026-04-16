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
import de.visualdigits.newshomereader.data.model.newsfeeds.NewsFeedConfigurationEntity
import de.visualdigits.newshomereader.data.model.newsfeeds.NodeType
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.gap
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun NewsFeedNavigationNodes(
    isLandscape: Boolean,
    node: NewsFeedConfigurationEntity,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    when (node.type) {
        NodeType.folder -> {
            VerticalCollapsibleBox(
                modifier = Modifier
                    .fillMaxWidth(),
                title = node.name,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RectangleShape,
                containerPadding = 0.dp,
                onStateChange = { state ->
                    onAction(NewsHomeReaderAction.OnCollapsibleStateChange("group_${node.name}", state))
                },
                isExpanded = state.collapsibleState["group_${node.name}"] == true,
                content = {
                    if (isLandscape) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            node.children.forEach { child -> NewsFeedNavigationNodes(isLandscape, child, onAction) }
                        }
                    } else {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            node.children.forEach { child -> NewsFeedNavigationNodes(isLandscape, child, onAction) }
                        }
                    }
                }
            )

        }
        NodeType.leaf -> {
            IndicatorButton(
                modifier = Modifier,
                width = 200.dp - MaterialTheme.shapes.gap * 2,
                height = if (isLandscape) 50.dp else 30.dp,
                indicatorPosition = Alignment.CenterStart,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                text = node.name,
                textStyle = MaterialTheme.typography.bodySmall,
                buttonColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.extraSmall,
                selected = state.currentFeedName == node.name
            ) {
                onAction(
                    NewsHomeReaderAction.OnNewsFeedClicked(
                        feedName = node.name,
                        currentFeedConfiguration = node
                    )
                )
            }
        }
    }
}
