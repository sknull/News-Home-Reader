package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.container.VerticalCollapsibleBox
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_arrow_drop_down_24px
import de.visualdigits.compose.resources.icon_arrow_right_24px
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalog
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogItem
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsFeedCatalog(
    modifier: Modifier = Modifier,
    catalog: NewsFeedCatalog?,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    state: NewsHomeReaderState,
    uriHandler: UriHandler,
    displayTheme: DisplayThemeEnum,
    onCommonAction: (CommonAction) -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit,
    onSubscriptionChanged: (NewsFeedCatalogItem, Boolean) -> Unit
) {
    val rootLines = state.newsFeedGroups.flatMap { nfg ->
        nfg.subGroups.flatMap { sg ->
            sg.newsFeeds.map { f -> f.rootLine }
        }
    }

    PlatformVerticalScrollbarBox(
        boxModifier = modifier
            .fillMaxSize(),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        scrollbarModifier = Modifier
            .fillMaxHeight()
            .width(10.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
        "catalog",
        scrollPosition = scrollPosition,
        collapsibleState = state.collapsibleState,
        onCommonAction = onCommonAction
    ) {
        val categories = if (state.onlySubscribedFeeds) {
            catalog?.categories?.mapNotNull { category ->
                val feeds = category.feeds.filter { feed -> rootLines.contains(feed.rootLine) }
                val subCategories = category.subCategories.mapNotNull { subCategory ->
                    val subFeeds = subCategory.feeds.filter { feed -> rootLines.contains(feed.rootLine) }
                    if (subFeeds.isNotEmpty()) {
                        subCategory.copy(feeds = subFeeds)
                    } else {
                        null
                    }
                }
                if (subCategories.isNotEmpty()) {
                    category.copy(
                        subCategories = subCategories,
                        feeds = feeds
                    )
                } else {
                    null
                }
            }
        } else {
            catalog?.categories
        }

        categories?.sortedBy { c -> c.name.lowercase() }?.map { mainCategory ->
            Pair("catalog_category_${mainCategory.name}", @Composable {
                VerticalCollapsibleBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = mainCategory.name,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = MaterialTheme.shapes.small,
                    iconArrowRight = painterResource(Res.drawable.icon_arrow_right_24px),
                    iconArrowDown = painterResource(Res.drawable.icon_arrow_drop_down_24px),
                    onStateChange = { state ->
                        onAction(
                            NewsHomeReaderAction.OnCollapsibleStateChange(
                                "group_catalog_${mainCategory.name}",
                                state
                            )
                        )
                    },
                    isExpanded = state.collapsibleState["group_catalog_${mainCategory.name}"] == true,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                    ) {
                        mainCategory.subCategories.sortedBy { c -> c.name.lowercase() }.forEach { subCategory ->
                            VerticalCollapsibleBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 24.dp),
                                title = subCategory.name,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                shape = MaterialTheme.shapes.small,
                                iconArrowRight = painterResource(Res.drawable.icon_arrow_right_24px),
                                iconArrowDown = painterResource(Res.drawable.icon_arrow_drop_down_24px),
                                onStateChange = { state ->
                                    onAction(
                                        NewsHomeReaderAction.OnCollapsibleStateChange(
                                            "group_catalog_${mainCategory.name}_${subCategory.name}",
                                            state
                                        )
                                    )
                                },
                                isExpanded = state.collapsibleState["group_catalog_${mainCategory.name}_${subCategory.name}"] == true,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                                ) {
                                    subCategory.feeds.sortedBy { f -> f.name.lowercase() }.forEach { feed ->
                                        ListItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.onSurface,
                                                    MaterialTheme.shapes.small
                                                )
                                                .padding(MaterialTheme.shapes.gap),
                                            headlineContent = {
                                                val interactionSource = remember { MutableInteractionSource() }
                                                val isHovered by interactionSource.collectIsHoveredAsState()
                                                Text(
                                                    modifier = Modifier
                                                        .pointerHoverIcon(PointerIcon.Hand)
                                                        .hoverable(interactionSource)
                                                        .clickable {
                                                            uriHandler.openUri(feed.url)
                                                        },
                                                    text = feed.name,
                                                    color = if (isHovered) displayTheme.textColor else MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        textDecoration = if (isHovered) TextDecoration.Underline else TextDecoration.None
                                                    )
                                                )
                                            },
                                            supportingContent = {
                                                if (feed.descriptionShort.isNotEmpty()) {
                                                    Text(
                                                        modifier = Modifier,
                                                        text = feed.descriptionShort,
                                                        style = MaterialTheme.typography.titleSmall
                                                    )
                                                }
                                                if (feed.descriptionLong.isNotEmpty() && feed.descriptionLong != feed.descriptionShort) {
                                                    Text(
                                                        modifier = Modifier,
                                                        text = feed.descriptionLong,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            },
                                            leadingContent = {},
                                            trailingContent = {
                                                val interactionSource = remember { MutableInteractionSource() }
                                                var checked by remember { mutableStateOf(rootLines.contains(feed.rootLine)) }
                                                Switch(
                                                    checked = checked,
                                                    onCheckedChange = { v ->
                                                        checked = v
                                                        onSubscriptionChanged(feed, v)
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
                                            },
                                            colors = ListItemDefaults.colors(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            })
        } ?: listOf()
    }
}
