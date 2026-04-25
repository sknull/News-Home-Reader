package de.visualdigits.common.presentation.components.container

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_close_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_search_24px
import de.visualdigits.compose.resources.title_search
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlexibleSearchBar(
    modifier: Modifier = Modifier,
    state: NewsHomeReaderState,
    isLargeScreen: Boolean,
    onQueryChange: (String) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    if (isLargeScreen) {
        DockedSearchBar(
            modifier = modifier
                .padding(MaterialTheme.shapes.gap),
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.searchText,
                    onQueryChange = { v -> onQueryChange(v) },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { v -> expanded = v },
                    enabled = true,
                    placeholder = { Text(stringResource(Res.string.title_search)) },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                                .hoverable(interactionSource)
                                .clickable {
                                    expanded = !expanded
                                },
                            painter = painterResource(Res.drawable.icon_search_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    trailingIcon = {
                        if (state.searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                onQueryChange("")
                                expanded = false
                            }) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_delete_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors().inputFieldColors.copy(cursorColor = MaterialTheme.colorScheme.onSurface),
                    interactionSource = null,
                )
            },
            expanded = expanded,
            onExpandedChange = { v -> expanded = v },
            shape = MaterialTheme.shapes.extraSmall,
            colors = SearchBarDefaults.colors(),
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            content = content,
        )
    } else {
        SearchBar(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = if (expanded) 0.dp else MaterialTheme.shapes.gap),
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.searchText,
                    onQueryChange = { v -> onQueryChange(v) },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { v -> expanded = v },
                    enabled = true,
                    placeholder = { Text(stringResource(Res.string.title_search)) },
                    leadingIcon = { Icon(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            .hoverable(interactionSource)
                            .clickable {
                                expanded = !expanded
                            },
                        painter = painterResource(Res.drawable.icon_search_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    ) },
                    trailingIcon = {
                        if (expanded) {
                            IconButton(onClick = {
                                onQueryChange("")
                                expanded = false
                            }) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_close_24px),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors().inputFieldColors.copy(cursorColor = MaterialTheme.colorScheme.onSurface),
                    interactionSource = null,
                )
            },
            expanded = expanded,
            onExpandedChange = { v -> expanded = v },
            shape = MaterialTheme.shapes.extraSmall,
            colors = SearchBarDefaults.colors(),
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = content,
        )
    }
}
