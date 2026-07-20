package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_counter_1_24px
import de.visualdigits.compose.resources.icon_counter_2_24px
import de.visualdigits.compose.resources.icon_done_all_24px
import de.visualdigits.compose.resources.icon_refresh_24px
import de.visualdigits.compose.resources.tooltip_mark_read_all
import de.visualdigits.compose.resources.tooltip_mark_read_older_1
import de.visualdigits.compose.resources.tooltip_mark_read_older_2
import de.visualdigits.compose.resources.tooltip_refresh_newsfeed
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.util.getFaviconUrl
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewsListMenuBar(
    modifier: Modifier = Modifier,
    viewModel: NewsHomeReaderViewModel,
    connectivityManager: ConnectivityManager,
    state: NewsHomeReaderState,
    maxWidth: Dp,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.currentNewsFeedName != null || state.currentNewsFeedGroup != null) {
            val path = mutableListOf<String>()
            if (state.currentNewsFeedGroup?.parentGroupName != null) {
                path.add(state.currentNewsFeedGroup.parentGroupName!!)
            }
            if (state.currentNewsFeedGroup?.name != null) {
                path.add(state.currentNewsFeedGroup.name)
            }
            if (state.currentNewsFeedName != null) {
                path.add(state.currentNewsFeedName)
            }
            val feedPath = path.joinToString(" / ")
            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_counter_2_24px),
                leadingIconTint = MaterialTheme.colorScheme.onSurface,
                toolTip = stringResource(Res.string.tooltip_mark_read_older_2)
            ) {
                onAction(NewsHomeReaderAction.OnMarkReadClicked(2))
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_counter_1_24px),
                leadingIconTint = MaterialTheme.colorScheme.onSurface,
                toolTip = stringResource(Res.string.tooltip_mark_read_older_1)
            ) {
                onAction(NewsHomeReaderAction.OnMarkReadClicked(1))
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_done_all_24px),
                leadingIconTint = MaterialTheme.colorScheme.onSurface,
                toolTip = stringResource(Res.string.tooltip_mark_read_all)
            ) {
                onAction(NewsHomeReaderAction.OnMarkReadClicked(0))
            }

            val wifiOnly = settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
            if (!wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge) {
                IndicatorButton(
                    modifier = Modifier,
                    width = 30.dp,
                    height = 30.dp,
                    padding = 2.dp,
                    leadingIcon = painterResource(Res.drawable.icon_refresh_24px),
                    leadingIconTint = MaterialTheme.colorScheme.onSurface,
                    toolTip = stringResource(Res.string.tooltip_refresh_newsfeed),
                ) {
                    onAction(NewsHomeReaderAction.OnNewsFeedRefresh(state.currentNewsFeedName, state.currentNewsFeedItem?.url))
                }
            }

            if (state.currentNewsFeedName != null) {
                state.lookupNewsFeedMap[state.currentNewsFeedName.trim().lowercase()]?.url?.let { url ->
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    ) {
                        Image(
                            modifier = Modifier,
                            url = url.getFaviconUrl(48),
                            width = 30.dp,
                            height = 30.dp,
                            contentDescription = state.currentNewsFeedName,
                            maxImageSize = 48,
                            showLoadingIcon = false
                        )
                    }
                }
            }

            Text(
                text = feedPath,
                style = if (maxWidth > 600.dp) MaterialTheme.typography.headlineMedium else  MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
