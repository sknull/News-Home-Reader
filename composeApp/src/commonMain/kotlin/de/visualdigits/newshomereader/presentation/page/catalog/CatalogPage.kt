package de.visualdigits.newshomereader.presentation.page.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel

@Composable
fun CatalogPage(
    state: NewsHomeReaderState,
    screenWidth: Dp,
    onAction: (NewsHomeReaderAction) -> Unit,
    viewModel: NewsHomeReaderViewModel,
    uriHandler: UriHandler,
    onCommonAction: (CommonAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 4.dp),
    ) {
        CatalogSearchBar(
            state = state,
            screenWidth = screenWidth,
            onAction = onAction,
            viewModel = viewModel,
            uriHandler = uriHandler,
            onCommonAction = onCommonAction
        )

        NewsFeedCatalog(
            modifier = Modifier,
            scrollPosition = viewModel.scrollPosition,
            catalog = state.newsFeedCatalog,
            state = state,
            uriHandler = uriHandler,
            onCommonAction = onCommonAction,
            onAction = onAction,
            onSubscriptionChanged = { newsFeedCatalogItem, subscribe ->
                onAction(NewsHomeReaderAction.OnSubscriptionChanged(newsFeedCatalogItem, subscribe))
            }
        )
    }
}
