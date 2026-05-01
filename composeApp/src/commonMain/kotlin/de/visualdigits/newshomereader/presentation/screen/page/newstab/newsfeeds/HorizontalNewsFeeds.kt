package de.visualdigits.newshomereader.presentation.screen.page.newstab.newsfeeds

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.screen.page.newstab.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun HorizontalNewsFeeds(
    state: NewsHomeReaderState,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    displayTheme: DisplayThemeEnum,
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
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.shapes.gap),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
    ) {
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
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
                            boxModifier = Modifier
                                .width(500.dp),
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
                            state.newsFeedGroups.map { newsFeedGroup ->
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
                boxModifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                scrollbarModifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxHeight()
                    .width(10.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
                scrollbarId = "newsfeed_${state.currentNewsFeedName}",
                scrollPosition = scrollPosition,
                collapsibleState = state.collapsibleState,
                onCommonAction
            ) {
                rowData.map { (_, rowItems) ->
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
