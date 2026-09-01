package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.navigation.NewsFeedGroupBox
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the news item card for a given newsfeed
 * in landscape mode.
 */
@Composable
fun NewsFeedsLandscape(
    viewModel: NewsHomeReaderViewModel,
    state: NewsHomeReaderState,
    platformType: PlatformType,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
    connectivityManager: ConnectivityManager,
    maxWidth: Dp,
    rowData: List<List<NewsItem>>,
    maxImageSize: Int?,
    uriHandler: UriHandler,
    columns: Int,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //
        // navigation
        //
        if (state.collapsibleState["newsfeed_items"] == true) {
            val borderColor = MaterialTheme.colorScheme.onSurface
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(if (state.isEditMode) 320.dp else 250.dp)
                    .conditional(platformType != PlatformType.jvm) { drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        drawLine(
                            color = borderColor,
                            start = Offset(size.width - strokeWidth / 2, 0f),
                            end = Offset(size.width - strokeWidth / 2, size.height - strokeWidth / 2),
                            strokeWidth = strokeWidth
                        )
                    }
                }
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
                        .fillMaxWidth(),
                    platformType = platformType,
                    scrollbarModifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .width(10.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                    scrollbarId = "newsfeed_navigation",
                    scrollPosition = scrollPosition,
                    onCommonAction = onCommonAction,
                    verticalArrangementGap = MaterialTheme.shapes.gap
                ) {
                    state.newsFeedGroups
                        .map { newsFeedGroup ->
                            Pair("newsfeed_navigation_${newsFeedGroup.name}", @Composable {
                                NewsFeedGroupBox(
                                    modifier = Modifier,
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

        //
        // Main area
        //
        val gap = MaterialTheme.shapes.gap
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .conditional(platformType != PlatformType.android) { padding(end = gap) }
        ) {
            if (state.currentNewsFeedGroup != null || state.currentNewsFeedName != null) {
                NewsListMenuBar(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background),
                    viewModel = viewModel,
                    connectivityManager = connectivityManager,
                    state = state,
                    maxWidth = maxWidth,
                    onAction = onAction
                )
                Spacer(Modifier.size(MaterialTheme.shapes.gap))
            }

            PlatformVerticalScrollbarBox(
                modifier = Modifier
                    .fillMaxSize(),
                platformType = platformType,
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .width(10.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                scrollbarId = "newsfeed_items",
                scrollPosition = scrollPosition,
                onCommonAction = onCommonAction
            ) {
                rowData.mapIndexed{ index, rowItems ->
                    Pair("row_${index}_" + rowItems.joinToString("_") { item -> item.id.toString() }, @Composable {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.shapes.gap),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            rowItems.forEach { newsItem ->
                                NewsItemCard(
                                    modifier = Modifier.weight(1f),
                                    viewModel = viewModel,
                                    state = state,
                                    maxImageSize = maxImageSize,
                                    newsItem = newsItem,
                                    uriHandler = uriHandler,
                                    onAction = onAction
                                )
                            }
                            (0 until columns - rowItems.size).forEach {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    })
                }
            }
        }
    }
}
