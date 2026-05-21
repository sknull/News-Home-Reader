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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.util.copyFactor
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.modifier.angledInnerShadow
import de.visualdigits.common.presentation.components.modifier.tintedBackgroundImage
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.circuit_board_squared
import de.visualdigits.compose.resources.circuit_board_with_circle
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
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the news item card for a given newsfeed
 * in landscape mode.
 */
@Composable
fun HorizontalNewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
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
    val dimFactor = if (displayTheme == DisplayThemeEnum.ANTHRACITE) 1.5f else 1.25f
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //
        // navigation
        //
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(if (state.isEditMode) 320.dp else 250.dp)
                        .padding(bottom = 220.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            scrollbarStyle = scrollbarStyle(),
                            scrollbarId = "newsfeed_navigation",
                            scrollPosition = scrollPosition,
                            onCommonAction = onCommonAction,
                            verticalArrangementGap = 0.dp
                        ) {
                            state.newsFeedGroups
                                .sortedBy { nfg -> nfg.name }
                                .map { newsFeedGroup ->
                                    Pair("newsfeed_navigation_${newsFeedGroup.name}", @Composable {
                                        NewsFeedGroupBox(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
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
                        .width(if (state.isEditMode) 320.dp else 250.dp)
                        .height(220.dp)
                        .tintedBackgroundImage(
                            image = imageResource(Res.drawable.circuit_board_with_circle),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentScale = ContentScale.Fit,
                            finalZoomFactor = 2.0f,
                            finalOffsetX = 4.dp,
                            finalOffsetY = 6.dp,
                            finalAlpha = 0.6f
                        )
                        .angledInnerShadow(
                            angle = 45f,
                            distance = 10.dp,
                            alpha = 0.5f,
                            insetSize = 2.dp,
                            insetColorLight = MaterialTheme.colorScheme.background.copyFactor(valueFactor = dimFactor),
                            insetColorShadow = MaterialTheme.colorScheme.background.copyFactor(valueFactor = 1f / dimFactor)
                        )
                        .padding(top = 8.dp, start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StudioClock(
                        modifier = Modifier
                            .width(180.dp)
                            .height(180.dp),
                        fontFamily = FontFamily(Font(Res.font.digital_dream_skew_fat)),
                        showSeconds = false,
                        showDate = true,
                        showYear = true,
                        colors = state.settings?.get<Color>(SK.spotColor)
                            ?.let { sc -> studioClockColors(sc) }
                            ?: studioClockColors(DisplayThemeEnum.SPOT_COLOR_DEFAULT)
                    )
                }
            }
        }

        //
        // Main area
        //
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                )
        ) {
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

            PlatformVerticalScrollbarBox(
                modifier = Modifier
                    .fillMaxWidth(),
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .width(10.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                scrollbarStyle = scrollbarStyle(),
                scrollbarId = "newsfeed_items",
                scrollPosition = scrollPosition,
                onCommonAction = onCommonAction
            ) {
                rowData.map { rowItems ->
                    Pair(rowItems.joinToString("_") { item -> item.id.toString() }, @Composable {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.shapes.gap),
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
