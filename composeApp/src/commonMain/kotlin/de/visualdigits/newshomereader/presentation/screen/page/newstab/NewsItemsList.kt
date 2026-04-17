package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbar
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_counter_1_24px
import de.visualdigits.compose.resources.icon_counter_2_24px
import de.visualdigits.compose.resources.icon_done_all_24px
import de.visualdigits.compose.resources.icon_refresh_24px
import de.visualdigits.compose.resources.tooltip_mark_read_all
import de.visualdigits.compose.resources.tooltip_mark_read_older_1
import de.visualdigits.compose.resources.tooltip_mark_read_older_2
import de.visualdigits.compose.resources.tooltip_refresh_newsfeed
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun NewsItemsList(
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    maxWidth: Dp,
    maxHeight: Dp,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState(viewModel.scrollPosition["newsfeed_${state.currentFeedName}"]?:0)
    LaunchedEffect(scrollState.value) {
        onAction(NewsHomeReaderAction.OnScrollPositionChange("newsfeed_${state.currentFeedName}", scrollState.value))
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
    ) {
        NewsListMenuBar(
            connectivityManager = connectivityManager,
            state = state,
            isLandscape = isLandscape,
            onAction = onAction
        )

        // scrollbar box
        val chunks = if (maxWidth > 1000.dp) {
            3
        } else if (maxWidth > 500.dp) {
            2
        } else {
            1
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(MaterialTheme.shapes.gap)
                    .verticalScroll(scrollState)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                state.visibleNewsItems
                    .chunked(chunks)
                    .forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            row.forEach { item ->
                                    NewsItemCard(
                                        modifier = modifier.weight(1f),
                                        maxWidth = maxWidth,
                                        maxHeight = maxHeight,
                                        newsItem = item,
                                        onAction = onAction
                                    )
                                }
                        }
                    }
            }

            PlatformVerticalScrollbar(
                interactionSource = interactionSource,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxHeight()
                    .width(10.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
                    .align(Alignment.CenterEnd),
                scrollState = scrollState
            )
        }
    }
}

@Composable
private fun NewsListMenuBar(
    connectivityManager: ConnectivityManager,
    state: NewsHomeReaderState,
    isLandscape: Boolean,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.currentFeedName != null) {
            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_counter_2_24px),
                toolTip = stringResource(Res.string.tooltip_mark_read_older_2)
            ) {
                onAction(NewsHomeReaderAction.OnMarkReadClicked(2))
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_counter_1_24px),
                toolTip = stringResource(Res.string.tooltip_mark_read_older_1)
            ) {
                onAction(NewsHomeReaderAction.OnMarkReadClicked(1))
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_done_all_24px),
                toolTip = stringResource(Res.string.tooltip_mark_read_all)
            ) {
                onAction(NewsHomeReaderAction.OnMarkReadClicked(0))
            }

            val wifiOnly = state.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
            IndicatorButton(
                modifier = Modifier,
                enabled = !wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_refresh_24px),
                toolTip = stringResource(Res.string.tooltip_refresh_newsfeed),
            ) {
                onAction(NewsHomeReaderAction.OnNewsFeedRefresh(state.currentFeedName, state.currentFeedConfiguration?.url))
            }

            Text(
                text = state.currentFeedName ?: "",
                style = if (isLandscape) MaterialTheme.typography.headlineMedium else  MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
