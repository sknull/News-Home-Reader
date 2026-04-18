package de.visualdigits.newshomereader.presentation.screen.page.newstab

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun NewsFeedsNavigation(
    state: NewsHomeReaderState,
    isLandscape: Boolean,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    if (isLandscape) {
        if (state.collapsibleState["group_newsfeeds_navigation"] == true) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(MaterialTheme.shapes.gap)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewsFeedNavigationNodes(state, true, state.newsFeedGroups, onAction)

                Spacer(Modifier.weight(1f))

                StudioClock(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp),
                    showSeconds = false,
                    showDate = true
                )
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
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewsFeedNavigationNodes(state, false, state.newsFeedGroups, onAction)
            }
        }
    }
}
