package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
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
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun NewsItemsList(
    modifier: Modifier = Modifier,
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    isLandscape: Boolean,
    maxWidth: Dp,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
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

        val chunks = when {
            maxWidth > 1500.dp -> 4
            maxWidth > 1000.dp -> 3
            maxWidth > 500.dp -> 2
            else -> 1
        }
        val rowData = remember(state.visibleNewsItems, chunks) {
            state.visibleNewsItems.chunked(chunks).map { rowItems ->
                val rowKey = rowItems.joinToString("_") { it.identifier }
                rowKey to rowItems
            }
        }

        PlatformVerticalScrollbarBox(
            boxModifier = Modifier
                .fillMaxWidth(),
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .fillMaxHeight()
                .width(10.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
            "newsfeed_${state.currentFeedName}",
            scrollPosition = scrollPosition,
            onAction
        ) {
            rowData.map { (_, rowItems) ->
                Pair(rowItems.joinToString("_") { item -> item.id.toString() }, @Composable {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                    ) {
                        rowItems.forEach { item ->
                            NewsItemCard(
                                modifier = Modifier.weight(1f),
                                maxImageSize = maxImageSize,
                                settings = settings,
                                uriHandler = uriHandler,
                                newsItem = item,
                                onAction = onAction
                            )
                        }
                        (0 until chunks - rowItems.size).forEach {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                })
            }
        }
    }
}

