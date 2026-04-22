package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.typography
import de.visualdigits.common.presentation.components.BindBackHandler
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.MyShapes
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.max

@Composable
fun MainPage(
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
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
            .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
    ) {
        val density = LocalDensity.current
        val mw = maxWidth
        val mh = maxHeight

        val sizeFactor = when {
            mw < 500.dp -> 0.9f
            mw > 1500.dp -> 1.5f
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
            LaunchedEffect(mw, mh) {
                // Umrechnung von Dp in Pixel für Coil
                val wPx = with(density) { mw.roundToPx() }
                val hPx = with(density) { mh.roundToPx() }
                onAction(NewsHomeReaderAction.UpdateMaxImageSize(state.settings, max(wPx, hPx)))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(displayTheme.colorScheme.background)
                    .safeDrawingPadding()
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
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        NewsContent(
                            state = state,
                            viewModel = viewModel,
                            mw = mw,
                            maxImageSize = maxImageSize,
                            uriHandler = uriHandler,
                            onAction = onAction,
                            connectivityManager = connectivityManager,
                            displayTheme = displayTheme
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
