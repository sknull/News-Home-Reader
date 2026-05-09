package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.digital_dream_skew_fat
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.navigation.NewsFeedGroupBox
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the news item card for a given newsfeed
 * in landscape mode.
 */
@Composable
fun HorizontalNewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    displayTheme: DisplayThemeEnum,
    connectivityManager: ConnectivityManager,
    maxWidth: Dp,
    rowData: List<List<NewsItem>>,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler,
    chunks: Int,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.shapes.gap),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
    ) {
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(if (state.isEditMode) 400.dp else 250.dp)
                        .padding(bottom = 200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
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
                            modifier = Modifier
                                .width(500.dp),
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
                            state.newsFeedGroups
                                .sortedBy { nfg -> nfg.name }
                                .map { newsFeedGroup ->
                                    Pair("newsfeed_navigation_${newsFeedGroup.name}", @Composable {
                                        NewsFeedGroupBox(
                                            newsFeedGroup = newsFeedGroup,
                                            onAction = onAction,
                                            state = state,
                                            maxImageSize = maxImageSize
                                        )
                                    })
                                }
                        }
                    }
                }

                StudioClock(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp),
                    fontFamily = FontFamily(Font(Res.font.digital_dream_skew_fat)),
                    showSeconds = false,
                    showDate = true,
                    colors = displayTheme.studioClockColors
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
        ) {
            NewsListMenuBar(
                connectivityManager = connectivityManager,
                state = state,
                maxWidth = maxWidth,
                onAction = onAction
            )

            PlatformVerticalScrollbarBox(
                modifier = Modifier
                    .fillMaxWidth(),
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .width(10.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                scrollbarStyle = scrollbarStyle(),
                scrollbarId = "newsfeed_${state.currentNewsFeedName}",
                scrollPosition = scrollPosition,
                onCommonAction = onCommonAction
            ) {
                rowData.map { rowItems ->
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
    }
}
