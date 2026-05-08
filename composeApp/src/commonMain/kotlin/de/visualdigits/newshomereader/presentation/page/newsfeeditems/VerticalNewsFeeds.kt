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
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.container.FlexibleSearchBar
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_close_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_search_24px
import de.visualdigits.compose.resources.title_search
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.navigation.NewsFeedGroupBox
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the news item card for a given newsfeed
 * in portrait mode.
 */
@Composable
fun VerticalNewsFeeds(
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    state: NewsHomeReaderState,
    connectivityManager: ConnectivityManager,
    screenWidth: Dp,
    maxWidth: Dp,
    rowData: List<List<NewsItem>>,
    rowDataFiltered: List<NewsItem>,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    chunks: Int,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    PlatformVerticalScrollbarBox(
        modifier = Modifier
            .fillMaxSize(),
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
            Pair("newslist_searchbar", @Composable {
                NewsItemSearchBar(
                    state,
                    screenWidth,
                    onAction,
                    scrollPosition,
                    onCommonAction,
                    rowDataFiltered,
                    maxImageSize,
                    settings,
                    uriHandler
                )
            }),
            Pair("newslist_menubar", @Composable {
                NewsListMenuBar(
                    connectivityManager = connectivityManager,
                    state = state,
                    maxWidth = maxWidth,
                    onAction = onAction
                )
            })
        ) + rowData.map { rowItems ->
            Pair(rowItems.joinToString("_") { item -> item.id.toString() }, @Composable {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {
                    rowItems.forEach { newsItem ->
                        NewsItemCard(
                            modifier = Modifier.weight(1f),
                            state = state,
                            maxImageSize = maxImageSize,
                            newsItem = newsItem,
                            settings = settings,
                            uriHandler = uriHandler,
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


