package de.visualdigits.newshomereader.presentation.screen.page.newstab.newsfeeds

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.screen.page.newstab.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.screen.page.newstab.newslist.NewsListMenuBar
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun VerticalNewsFeeds(
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    state: NewsHomeReaderState,
    connectivityManager: ConnectivityManager,
    maxWidth: Dp,
    rowData: List<Pair<String, List<NewsItem>>>,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    chunks: Int,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    PlatformVerticalScrollbarBox(
        boxModifier = Modifier
            .fillMaxSize(),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        scrollbarModifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .width(10.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
        "newsfeed_navigation",
        scrollPosition = scrollPosition,
        collapsibleState = state.collapsibleState,
        onCommonAction
    ) {
        val bool = state.collapsibleState["group_newsfeeds_navigation"]
        if (bool == true) {
            listOf(Pair("newsfeed_navigation", @Composable {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(MaterialTheme.shapes.gap)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {
                    state.newsFeedGroups.forEach { newsFeedGroup ->
                        NewsFeedGroupBox(newsFeedGroup, onAction, state)
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
        ) + rowData.map { (_, rowItems) ->
            Pair(rowItems.joinToString("_") { item -> item.id.toString() }, @Composable {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {
                    rowItems.forEach { item ->
                        NewsItemCard(
                            modifier = Modifier.weight(1f),
                            state = state,
                            maxImageSize = maxImageSize,
                            newsItem = item,
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

