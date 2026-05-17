package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import de.visualdigits.common.presentation.model.ScrollIntent
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_close_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_search_24px
import de.visualdigits.compose.resources.title_search
import de.visualdigits.compose.resources.warning_no_results
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.item.NewsItemCard
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.scrollbarStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewsItemSearchBar(
    state: NewsHomeReaderState,
    screenWidth: Dp,
    onAction: (NewsHomeReaderAction) -> Unit,
    scrollPosition: MutableMap<String, Triple<Int, Int?, ScrollIntent>>,
    onCommonAction: (CommonAction) -> Unit,
    rowDataFiltered: List<List<NewsItem>>,
    maxImageSize: Int?,
    displayTheme: DisplayThemeEnum?,
    uriHandler: UriHandler
) {
    FlexibleSearchBar(
        modifier = Modifier
            .fillMaxWidth(),
        titleSearch = UiText.StringResourceId(Res.string.title_search),
        iconClose = painterResource(Res.drawable.icon_close_24px),
        iconDelete = painterResource(Res.drawable.icon_delete_24px),
        iconSearch = painterResource(Res.drawable.icon_search_24px),
        searchText = state.newsItemSearchText?:"",
        isLargeScreen = screenWidth > 100.dp,
        isExpanded = state.isNewsItemSearchActive,
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
            scrollbarId = "newsfeed_searchbar",
            scrollPosition = scrollPosition,
            onCommonAction = onCommonAction
        ) {
            if (rowDataFiltered.isNotEmpty()) {
                rowDataFiltered.map { newsItems ->
                    Pair("search_item_${newsItems.joinToString("_") { ni -> ni.id.toString() }}", @Composable {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                        ) {
                            newsItems.forEach { newsItem ->
                                NewsItemCard(
                                    modifier = Modifier.weight(1f),
                                    simple = true,
                                    state = state,
                                    maxImageSize = maxImageSize,
                                    newsItem = newsItem,
                                    displayTheme = displayTheme,
                                    uriHandler = uriHandler,
                                    onAction = onAction
                                )
                            }
                        }
                    })
                }
            } else {
                listOf(Pair("search_item_empty", @Composable {
                    Text(
                        text = stringResource(Res.string.warning_no_results),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }))
            }
        }
    }
}
