package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.container.FlexibleSearchBar
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_close_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_search_24px
import de.visualdigits.compose.resources.title_search
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsItemSearchBar(
    state: NewsHomeReaderState,
    screenWidth: Dp,
    onAction: (NewsHomeReaderAction) -> Unit,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onCommonAction: (CommonAction) -> Unit,
    rowDataFiltered: List<NewsItem>,
    maxImageSize: Int?,
    settings: Settings?,
    uriHandler: UriHandler
) {
    FlexibleSearchBar(
        modifier = Modifier
            .fillMaxWidth(),
        titleSearch = UiText.StringResourceId(Res.string.title_search),
        iconClose = painterResource(Res.drawable.icon_close_24px),
        iconDelete = painterResource(Res.drawable.icon_delete_24px),
        iconSearch = painterResource(Res.drawable.icon_search_24px),
        searchText = state.newsItemSearchText,
        isLargeScreen = screenWidth > 100.dp,
        onExpandedChange = { expanded ->
            onAction(NewsHomeReaderAction.OnNewsItemSearchExpandStateChanged(expanded))
        },
        onQueryChange = { v ->
            onAction(NewsHomeReaderAction.OnNewsItemSearchTextChanged(v))
        }
    ) {
        PlatformVerticalScrollbarBox(
            modifier = Modifier
                .fillMaxWidth(),
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(10.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
            scrollbarStyle = scrollbarStyle(),
            scrollbarId = "newsfeed_${state.currentNewsFeedName}",
            scrollPosition = scrollPosition,
            onCommonAction = onCommonAction
        ) {
            rowDataFiltered.map { newsItem ->
                Pair("search_item_${newsItem.id}", @Composable {
                    NewsItemCard(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        simple = true,
                        maxImageSize = maxImageSize,
                        newsItem = newsItem,
                        settings = settings,
                        uriHandler = uriHandler,
                        onAction = onAction
                    )
                })
            }
        }
    }
}
