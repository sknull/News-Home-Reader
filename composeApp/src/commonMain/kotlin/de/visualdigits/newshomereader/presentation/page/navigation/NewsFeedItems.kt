package de.visualdigits.newshomereader.presentation.page.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.Image
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

/**
 * Renders the buttons for a given newsfeed group.
 */
@Composable
fun NewsFeedItems(
    modifier: Modifier = Modifier,
    newsFeedGroup: NewsFeedGroup,
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        newsFeedGroup.newsFeeds.forEach { newsFeedItem ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IndicatorButton(
                    width = 200.dp - MaterialTheme.shapes.gap * 2,
                    height = 50.dp,
                    indicatorPosition = Alignment.CenterStart,
                    indicatorColor = MaterialTheme.colorScheme.onSurface,
                    text = newsFeedItem.name,
                    textStyle = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    buttonColor = Color.Transparent,
                    shape = MaterialTheme.shapes.extraSmall,
                    selected = state.currentNewsFeedName == newsFeedItem.name,
                    leadingImage = {
                        newsFeedItem.url?.getFaviconUrl(48)?.let { url ->
                            Image(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(24.dp),
                                url = url,
                                contentDescription = newsFeedItem.name ?: "",
                                maxImageSize = 48,
                                showLoadingIcon = false
                            )
                        }
                    }
                ) {
                    onAction(
                        NewsHomeReaderAction.OnNewsFeedClicked(
                            feedName = newsFeedItem.name,
                            currentFeedIItem = newsFeedItem
                        )
                    )
                }

                if (state.isEditMode) {
                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_edit_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnEditNewsFeedConfigurationClick(newsFeedItem))
                    }

                    IndicatorButton(
                        modifier = Modifier,
                        width = 30.dp,
                        height = 30.dp,
                        padding = 2.dp,
                        leadingIcon = painterResource(Res.drawable.icon_delete_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnDeleteNewsFeedConfigurationClick(newsFeedItem))
                    }
                }
            }
        }
    }
}
