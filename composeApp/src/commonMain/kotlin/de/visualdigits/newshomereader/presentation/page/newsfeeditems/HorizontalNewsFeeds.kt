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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.digital_dream_skew_fat
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.navigation.NewsFeedGroupBox
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import de.visualdigits.newshomereader.presentation.style.studioClockColors
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
    uriHandler: UriHandler,
    chunks: Int,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
    ) {
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                val edgeColor = MaterialTheme.colorScheme.primaryFixedDim
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(if (state.isEditMode) 400.dp else 250.dp)
                        .padding(bottom = 220.dp)
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
                                .fillMaxWidth()
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

                Box(
                    modifier = Modifier
                        .width(if (state.isEditMode) 400.dp else 250.dp)
                        .height(220.dp)
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
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = strokeWidth
                            )
                        }
                        .padding(top = 8.dp, start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StudioClock(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp),
                        fontFamily = FontFamily(Font(Res.font.digital_dream_skew_fat)),
                        showSeconds = false,
                        showDate = true,
                        colors = state.settings?.get<Color>(SK.spotColor)
                            ?.let { sc -> studioClockColors(sc) }
                            ?: studioClockColors(DisplayThemeEnum.SPOT_COLOR_DEFAULT)
                    )
                }
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
                    .fillMaxWidth()
                    .padding(end = 8.dp),
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
                }
            }
        }
    }
}
