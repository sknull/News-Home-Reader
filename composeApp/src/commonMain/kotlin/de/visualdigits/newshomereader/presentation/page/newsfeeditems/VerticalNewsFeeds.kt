package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.util.copyFactor
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.modifier.angledInnerShadow
import de.visualdigits.common.presentation.components.modifier.tintedBackgroundImage
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.circuit_board_squared
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.navigation.NewsFeedGroupBox
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.imageResource

/**
 * Renders the news item card for a given newsfeed
 * in portrait mode.
 */
@Composable
fun VerticalNewsFeeds(
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
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
    val dimFactor = if (displayTheme == DisplayThemeEnum.ANTHRACITE) 1.5f else 1.25f
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PlatformVerticalScrollbarBox(
            modifier = Modifier
                .fillMaxSize()
                .tintedBackgroundImage(
                    image = imageResource(Res.drawable.circuit_board_squared),
                    tint = MaterialTheme.colorScheme.onSurface,
                    finalAlpha = 0.2f
                )
                .angledInnerShadow(
                    angle = 45f,
                    distance = 20.dp,
                    spread = 10.dp,
                    alpha = 0.5f,
                    insetSize = 2.dp,
                    insetColorLight = MaterialTheme.colorScheme.background.copyFactor(valueFactor = dimFactor),
                    insetColorShadow = MaterialTheme.colorScheme.background.copyFactor(valueFactor = 1f / dimFactor)
                ),
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(10.dp)
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
            padding = 0.dp,
            verticalArrangementGap = 0.dp,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
            scrollbarStyle = scrollbarStyle(),
            scrollbarId = "newsfeed_items",
            scrollPosition = scrollPosition,
            onCommonAction = onCommonAction,
            scrollToTop = { scrollState, scrollIntent ->
                LaunchedEffect(state.collapsibleState["group_newsfeeds_navigation"], scrollIntent) {
                    if (state.collapsibleState["group_newsfeeds_navigation"] == true || scrollIntent != ScrollIntent.standard) {
                        scrollState.animateScrollTo(0)
                    }
                }
            },
            scrollToTopLazy = { lazyListState, scrollIntent ->
                LaunchedEffect(state.collapsibleState["group_newsfeeds_navigation"], scrollIntent) {
                    if (state.collapsibleState["group_newsfeeds_navigation"] == true || scrollIntent != ScrollIntent.standard) {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            }
        ) {
            val lastRow = rowData.size - 1
            if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
                listOf(Pair("newsfeed_navigation", @Composable {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        state.newsFeedGroups
                            .sortedBy { nfg -> nfg.name }
                            .forEach { newsFeedGroup ->
                                NewsFeedGroupBox(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
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
                    if (state.currentNewsFeedGroup != null || state.currentNewsFeedName != null) {
                        NewsListMenuBar(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background),
                            connectivityManager = connectivityManager,
                            state = state,
                            maxWidth = maxWidth,
                            onAction = onAction
                        )
                        Spacer(Modifier.size(MaterialTheme.shapes.gap))
                    }
                })
            ) + rowData.flatMapIndexed{ index, rowItems ->
                listOf(
                    Pair("row_" + rowItems.joinToString("_") { item -> item.id.toString() }, @Composable {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.shapes.gap),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            rowItems.forEach{ newsItem ->
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
                    })
                ) + if (index < lastRow) {
                    listOf(Pair("spacer_" + rowItems.joinToString("_") { item -> item.id.toString() }, @Composable { Spacer(Modifier.size(MaterialTheme.shapes.gap)) }))
                } else {
                    listOf()
                }
            }
        }
    }
}


