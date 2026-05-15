package de.visualdigits.newshomereader.presentation.page.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_arrow_drop_down_24px
import de.visualdigits.compose.resources.icon_arrow_right_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the collapsible container for a given  newsfeed group.
 */
@Composable
fun NewsFeedGroupBox(
    newsFeedGroup: NewsFeedGroup,
    onAction: (NewsHomeReaderAction) -> Unit,
    state: NewsHomeReaderState,
    maxImageSize: Int?
) {
    val edgeColor = MaterialTheme.colorScheme.onSurface
    VerticalCollapsibleBox(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind() {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = edgeColor,
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            },
        titleContent = {
            IndicatorButton(
                width = 180.dp - MaterialTheme.shapes.gap * 2,
                height = 50.dp,
                indicatorPosition = Alignment.CenterStart,
                indicatorColor = MaterialTheme.colorScheme.onSurface,
                text = newsFeedGroup.name,
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                buttonColor = Color.Transparent,
                shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
                selected = state.currentNewsFeedGroup?.name == newsFeedGroup.name
            )
        },
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.small,
        onStateChange = { state ->
            onAction(NewsHomeReaderAction.OnNewsFeedGroupCollapsibleStateChange(newsFeedGroup, state))
        },
        isExpanded = state.collapsibleState["group_${newsFeedGroup.name}"] == true,
        iconArrowRight = painterResource(Res.drawable.icon_arrow_right_24px),
        iconArrowDown = painterResource(Res.drawable.icon_arrow_drop_down_24px),
        trailingIcon = {
            EditButtonsTop(
                state,
                onAction,
                newsFeedGroup
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        ) {
            newsFeedGroup.subGroups
                .sortedBy { sg -> sg.name }
                .forEach { subNewsFeedGroup ->
                NewsFeedGroupBox(
                    newsFeedGroup = subNewsFeedGroup,
                    onAction = onAction,
                    state = state,
                    maxImageSize = maxImageSize
                )
            }

            NewsFeedItems(
                newsFeedGroup = newsFeedGroup,
                state = state,
                onAction = onAction
            )

            EditButtonsBottom(
                state,
                onAction,
                newsFeedGroup
            )
        }
    }
}
