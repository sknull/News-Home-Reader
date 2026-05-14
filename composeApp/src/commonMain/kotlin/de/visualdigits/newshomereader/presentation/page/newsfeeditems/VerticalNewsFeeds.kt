package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.navigation.NewsFeedGroupBox
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle

/**
 * Renders the news item card for a given newsfeed
 * in portrait mode.
 */
@Composable
fun VerticalNewsFeeds(
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    state: NewsHomeReaderState,
    connectivityManager: ConnectivityManager,
    maxWidth: Dp,
    rowData: List<List<NewsItem>>,
    maxImageSize: Int?,
    displayTheme: DisplayThemeEnum?,
    uriHandler: UriHandler,
    chunks: Int,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val edgeColor = MaterialTheme.colorScheme.primaryFixedDim
    PlatformVerticalScrollbarBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 10.dp) // let shadow start before scrollbar
            .innerShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 6.dp,
                    spread = 2.dp,
                    color = Color.Black.copy(alpha = 0.2f),
                    offset = DpOffset(x = (-10).dp, y = 5.dp)
                )
            )
            .drawBehind() {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = edgeColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = edgeColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
            .padding(top = 8.dp), // push content a bit down
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        scrollbarModifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .width(10.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
        scrollbarStyle = scrollbarStyle(),
        scrollbarId = "newsfeed_navigation",
        scrollPosition = scrollPosition,
        onCommonAction = onCommonAction,
        scrollToTop = { lazyListState ->
            LaunchedEffect(state.collapsibleState["group_newsfeeds_navigation"]) {
                if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
                    lazyListState.animateScrollToItem(0)
                }
            }
        }
    ) {
        val bool = state.collapsibleState["group_newsfeeds_navigation"]
        if (bool == true) {
            listOf(Pair("newsfeed_navigation", @Composable {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(MaterialTheme.shapes.gap),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {
                    state.newsFeedGroups
                        .sortedBy { nfg -> nfg.name }
                        .forEach { newsFeedGroup ->
                            NewsFeedGroupBox(
                                newsFeedGroup = newsFeedGroup,
                                onAction = onAction,
                                state = state,
                                maxImageSize = maxImageSize
                            )
                        }
                }
            }))
        } else {
            listOf()
        } + listOf(
            Pair("newslist_menubar", @Composable {
                NewsListMenuBar(
                    connectivityManager = connectivityManager,
                    state = state,
                    maxWidth = maxWidth,
                    onAction = onAction
                )
            })
        ) + listOf(
            Pair("newslist_items", @Composable {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    rowData.map { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            rowItems.forEach { newsItem ->
                                NewsItemCard(
                                    modifier = Modifier
                                        .weight(1f),
                                    state = state,
                                    maxImageSize = maxImageSize,
                                    newsItem = newsItem,
                                    displayTheme = displayTheme,
                                    uriHandler = uriHandler,
                                    onAction = onAction
                                )
                            }
                            (0 until chunks - rowItems.size).forEach {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            })
        )
    }
}


