package de.visualdigits.newshomereader.presentation.screen.page.newstab.newsfeeds

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_24px
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsFeedsNavigation(
    state: NewsHomeReaderState,
    displayTheme: DisplayThemeEnum,
    isLandscape: Boolean,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    if (isLandscape) {
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(if (state.isEditMode) 400.dp else 200.dp)
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 200.dp)
                    ) {
                        NewsFeedNavigationNodes(
                            state = state,
                            newsFeedGroups = state.newsFeedGroups,
                            scrollPosition = scrollPosition,
                            onAction = onAction
                        )
                    }

                    StudioClock(
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp),
                        showSeconds = false,
                        showDate = true,
                        colors = displayTheme.studioClockColors
                    )
                }
            }
        }
    } else {
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                NewsFeedNavigationNodes(
                    state = state,
                    newsFeedGroups = state.newsFeedGroups,
                    scrollPosition = scrollPosition,
                    onAction = onAction
                )
            }
        }
    }
}
