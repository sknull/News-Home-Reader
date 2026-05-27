package de.visualdigits.newshomereader.presentation.page.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import de.visualdigits.common.presentation.components.util.conditional
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_ad_group_24px
import de.visualdigits.compose.resources.icon_arrow_drop_down_24px
import de.visualdigits.compose.resources.icon_arrow_right_24px
import de.visualdigits.compose.resources.icon_collections_bookmark_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
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
    modifier: Modifier = Modifier,
    newsFeedGroup: NewsFeedGroup,
    onAction: (NewsHomeReaderAction) -> Unit,
    state: NewsHomeReaderState,
    maxImageSize: Int?
) {
    val edgeColor = MaterialTheme.colorScheme.onSurface
    val collapsibleState = state.collapsibleState["group_${newsFeedGroup.name}"]
    VerticalCollapsibleBox(
        modifier = modifier
            .fillMaxWidth()
            .conditional(
                newsFeedGroup.parentGroupName == null
            ) {
                drawBehind() {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = edgeColor,
                        start = Offset(0f, size.height - strokeWidth / 2),
                        end = Offset(size.width, size.height - strokeWidth / 2),
                        strokeWidth = strokeWidth
                    )
                }
            }
            .padding(start = if (newsFeedGroup.parentGroupName != null) 20.dp else 0.dp),
        enabled = !newsFeedGroup.isKeywordBucket,
        isTitleHoverable = true,
        titleHoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        titleContent = {
            IndicatorButton(
                isHoverable = false,
                width = 180.dp - MaterialTheme.shapes.gap * 2,
                height = 50.dp,
                leadingIcon = if (newsFeedGroup.isKeywordBucket) painterResource(Res.drawable.icon_collections_bookmark_24px) else painterResource(Res.drawable.icon_ad_group_24px),
                indicatorPosition = Alignment.CenterStart,
                indicatorColor = MaterialTheme.colorScheme.onSurface,
                text = newsFeedGroup.name,
                textStyle = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                textColor = MaterialTheme.colorScheme.onSurface,
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
        isExpanded = collapsibleState == true,
        iconArrowRight = if (!newsFeedGroup.isKeywordBucket || newsFeedGroup.subGroups.isNotEmpty() || newsFeedGroup.newsFeeds.isNotEmpty()) painterResource(Res.drawable.icon_arrow_right_24px) else null,
        iconArrowDown = if (!newsFeedGroup.isKeywordBucket || newsFeedGroup.subGroups.isNotEmpty() || newsFeedGroup.newsFeeds.isNotEmpty()) painterResource(Res.drawable.icon_arrow_drop_down_24px) else null,
        trailingIcon = {
            if (state.isEditMode && newsFeedGroup.isEditable) {
                Row() {
                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_edit_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnEditNewsfeedGroupGroupClick(newsFeedGroup))
                    }

                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_delete_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsfeedGroupClick(newsFeedGroup))
                    }
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
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
                modifier = Modifier
                    .padding(start = 20.dp),
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
