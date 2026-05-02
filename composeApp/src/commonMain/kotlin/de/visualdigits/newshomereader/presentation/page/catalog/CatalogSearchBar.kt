package de.visualdigits.newshomereader.presentation.page.catalog

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.presentation.components.container.FlexibleSearchBar
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_close_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_search_24px
import de.visualdigits.compose.resources.title_search
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

@Composable
fun CatalogSearchBar(
    state: NewsHomeReaderState,
    screenWidth: Dp,
    onAction: (NewsHomeReaderAction) -> Unit,
    viewModel: NewsHomeReaderViewModel,
    uriHandler: UriHandler,
    displayTheme: DisplayThemeEnum,
    onCommonAction: (CommonAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.shapes.gap),
    ) {
        FlexibleSearchBar(
            modifier = Modifier
                .weight(1f),
            titleSearch = UiText.StringResourceId(Res.string.title_search),
            iconClose = painterResource(Res.drawable.icon_close_24px),
            iconDelete = painterResource(Res.drawable.icon_delete_24px),
            iconSearch = painterResource(Res.drawable.icon_search_24px),
            searchText = state.searchText,
            isLargeScreen = screenWidth > 100.dp,
            onQueryChange = { v ->
                onAction(NewsHomeReaderAction.OnSearchTextChanged(v))
            }
        ) {
            NewsFeedCatalog(
                modifier = Modifier,
                scrollPosition = viewModel.scrollPosition,
                catalog = state.filteredCatalog,
                state = state,
                uriHandler = uriHandler,
                displayTheme = displayTheme,
                onCommonAction = onCommonAction,
                onAction = onAction,
                onSubscriptionChanged = { newsFeedCatalogItem, subscribe ->
                    onAction(
                        NewsHomeReaderAction.OnSubscriptionChanged(
                            newsFeedCatalogItem,
                            subscribe
                        )
                    )
                }
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        var checked by remember { mutableStateOf(false) }
        Switch(
            checked = checked,
            onCheckedChange = { v ->
                checked = v
                onAction(NewsHomeReaderAction.OnOnlySubscribedFeeds(v))
            },
            interactionSource = interactionSource,
            colors = SwitchDefaults.colors().copy(
                checkedTrackColor = MaterialTheme.colorScheme.onSurface,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                checkedBorderColor = MaterialTheme.colorScheme.onSurface,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                uncheckedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}
