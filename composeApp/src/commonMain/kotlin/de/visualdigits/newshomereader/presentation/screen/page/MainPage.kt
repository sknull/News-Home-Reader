package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.typography
import de.visualdigits.common.presentation.components.BindBackHandler
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.common.presentation.components.container.FlexibleSearchBar
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.catalog.NewsFeedCatalogItem
import de.visualdigits.newshomereader.domain.model.errorhandling.kermitLogger
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsContent
import de.visualdigits.newshomereader.presentation.screen.page.settings.SettingsPage
import de.visualdigits.newshomereader.presentation.style.MyShapes
import de.visualdigits.newshomereader.presentation.style.gap
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val log = kermitLogger("MainPage")


    val viewModel: NewsHomeReaderViewModel = koinViewModel()

    val state by viewModel.state.collectAsState()
    val displayTheme = state.settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: DisplayThemeEnum.LIGHT
    val maxImageSize = state.settings?.get<Int>(SK.maxImageSize) ?: 1200

    val uriHandler = LocalUriHandler.current

    BindBackHandler(isEnabled = state.currentNewsArticle != null) {
        viewModel.onAction(NewsHomeReaderAction.OnNewsItemClosed())
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val density = LocalDensity.current
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val sizeFactor = when {
            screenWidth < 500.dp -> 0.9f
            screenWidth > 1500.dp -> 1.5f
            else -> 1.0f
        }

        MaterialTheme(
            colorScheme = displayTheme.colorScheme,
            typography = typography(
                textColor = displayTheme.textColor,
                sizeFactor = sizeFactor
            ),
            shapes = MyShapes
        ) {
            LaunchedEffect(screenWidth, screenHeight) {
                // Umrechnung von Dp in Pixel für Coil
                val wPx = with(density) { screenWidth.roundToPx() }
                val hPx = with(density) { screenHeight.roundToPx() }
                onAction(NewsHomeReaderAction.UpdateMaxImageSize(state.settings, max(wPx, hPx)))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(displayTheme.colorScheme.background)
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
            ) {
                ErrorCard(
                    errorMessage = state.uiMessage,
                    severity = state.uiMessageSeverity,
                    shapeContainer = MaterialTheme.shapes.small
                )

                MainMenuBar(
                    state = state,
                    onAction = onAction,
                    connectivityManager = connectivityManager
                )

                if (state.isShowInfos) {
                    InfoPage(uriHandler, onAction)
                } else if (state.isEditingSettings) {
                    SettingsPage(state, viewModel.scrollPosition, onAction)
                } else if (state.isAddingNewsFeedConfiguration || state.isEditingNewsFeedConfiguration) {
                    NewsFeedConfigurationPage(state, viewModel, onAction)
                } else if (state.isViewingCatalog) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.shapes.gap),
                        ) {
                            FlexibleSearchBar(
                                modifier = Modifier
                                    .weight(1f),
                                state = state,
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
                                    onAction = onAction,
                                    onSubscriptionChanged = { newsFeedCatalogItem, subscribe ->
                                        onAction(NewsHomeReaderAction.OnSubscriptionChanged(newsFeedCatalogItem, subscribe))
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

                        NewsFeedCatalog(
                            modifier = Modifier,
                            scrollPosition = viewModel.scrollPosition,
                            catalog = state.newsFeedCatalog,
                            state = state,
                            uriHandler = uriHandler,
                            displayTheme = displayTheme,
                            onAction = onAction,
                            onSubscriptionChanged = { newsFeedCatalogItem, subscribe ->
                                onAction(NewsHomeReaderAction.OnSubscriptionChanged(newsFeedCatalogItem, subscribe))
                            }
                        )
                    }
                } else {
                    NewsContent(
                        state = state,
                        viewModel = viewModel,
                        mw = screenWidth,
                        maxImageSize = maxImageSize,
                        uriHandler = uriHandler,
                        onAction = onAction,
                        connectivityManager = connectivityManager,
                        displayTheme = displayTheme
                    )
                }
            }

            NewsFeedConfigurationGroupDialog(
                state = state,
                onAction = onAction
            )

            ConfirmDeleteNewsFeedGroupDialog(
                state = state,
                onAction = onAction
            )
        }
    }
}
