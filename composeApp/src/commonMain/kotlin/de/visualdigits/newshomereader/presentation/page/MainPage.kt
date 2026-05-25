package de.visualdigits.newshomereader.presentation.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.BindBackHandler
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.common.presentation.model.CommonAction
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.page.catalog.CatalogPage
import de.visualdigits.newshomereader.presentation.page.newsfeedconfiguration.ConfirmDeleteNewsFeedGroupDialog
import de.visualdigits.newshomereader.presentation.page.newsfeedconfiguration.NewsFeedConfigurationGroupDialog
import de.visualdigits.newshomereader.presentation.page.newsfeedconfiguration.NewsFeedConfigurationPage
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.NewsContent
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.NewsItemSearchBar
import de.visualdigits.newshomereader.presentation.page.settings.SettingsPage
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum.ANTHRACITE
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum.LIGHT
import de.visualdigits.newshomereader.presentation.style.MyShapes
import de.visualdigits.newshomereader.presentation.style.anthraciteTheme
import de.visualdigits.newshomereader.presentation.style.lightTheme
import de.visualdigits.newshomereader.presentation.style.typography
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: NewsHomeReaderViewModel,
    connectivityManager: ConnectivityManager
) {
    val state by viewModel.state.collectAsState()
    val displayTheme = state.settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: LIGHT
    val spotColor = state.settings?.get<Color>(SK.spotColor)?: DisplayThemeEnum.SPOT_COLOR_DEFAULT
    val maxImageSize = state.settings?.get<Int>(SK.maxImageSize) ?: 1200

    val uriHandler = LocalUriHandler.current

    val onCommonAction: (CommonAction) -> Unit = { action ->
        viewModel.onCommonAction(action)
    }
    val onAction: (NewsHomeReaderAction) -> Unit = { action ->
        viewModel.onAction(action)
    }

    BindBackHandler(isEnabled = state.currentNewsArticle != null) {
        viewModel.onAction(NewsHomeReaderAction.OnNewsItemClosed())
    }

    val colorScheme = when (displayTheme) {
        LIGHT -> lightTheme(spotColor)
        ANTHRACITE -> anthraciteTheme(spotColor)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val density = LocalDensity.current
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val sizeFactor = when {
            screenWidth < 500.dp -> 0.9f
//            screenWidth > 1500.dp -> 1.5f
            else -> 1.0f
        }

        val chunks = when {
            maxWidth > 1500.dp -> 4
            maxWidth > 1000.dp -> 3
            maxWidth > 500.dp -> 2
            else -> 1
        }

        val rowDataFiltered = remember(state.filteredNewsItems) {
            state.filteredNewsItems
                .sortedByDescending { newsItem -> newsItem.updated }
                .chunked(chunks)
        }

        MaterialTheme(
            colorScheme = colorScheme,
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .safeDrawingPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 4.dp, bottom = 4.dp),
                ) {
                    ErrorCard(
                        errorMessage = state.uiMessage,
                        severity = state.uiMessageSeverity,
                        shapeContainer = MaterialTheme.shapes.small
                    )

                    NewsItemSearchBar(
                        state = state,
                        screenWidth = screenWidth,
                        onAction = onAction,
                        scrollPosition = viewModel.scrollPosition,
                        onCommonAction = onCommonAction,
                        rowDataFiltered = rowDataFiltered,
                        maxImageSize = maxImageSize,
                        displayTheme = displayTheme,
                        uriHandler = uriHandler
                    )

                    MainMenuBar(
                        state = state,
                        onAction = onAction,
                        connectivityManager = connectivityManager
                    )

                    when {
                        state.isShowInfos -> {
                            InfoPage(
                                uriHandler = uriHandler,
                                onAction = onAction
                            )
                        }
                        state.isEditingSettings -> {
                            SettingsPage(
                                viewModel = viewModel,
                                state = state,
                                scrollPosition = viewModel.scrollPosition,
                                onCommonAction = onCommonAction,
                                onAction = onAction
                            )
                        }
                        state.isAddingNewsFeedConfiguration || state.isEditingNewsFeedConfiguration -> {
                            NewsFeedConfigurationPage(
                                state = state,
                                viewModel = viewModel,
                                onCommonAction = onCommonAction,
                                onAction = onAction
                            )
                        }
                        state.isViewingCatalog -> {
                            CatalogPage(
                                state = state,
                                screenWidth = screenWidth,
                                onAction = onAction,
                                viewModel = viewModel,
                                uriHandler = uriHandler,
                                displayTheme = displayTheme,
                                onCommonAction = onCommonAction
                            )
                        }
                        else -> {
                            NewsContent(
                                state = state,
                                chunks = chunks,
                                viewModel = viewModel,
                                maxWidth = screenWidth,
                                maxHeight = screenHeight,
                                maxImageSize = maxImageSize,
                                uriHandler = uriHandler,
                                connectivityManager = connectivityManager,
                                displayTheme = displayTheme,
                                onCommonAction = onCommonAction,
                                onAction = onAction
                            )
                        }
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
}
