package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_info_24px
import de.visualdigits.compose.resources.icon_settings_24px
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsArticleCard
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsFeeds
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainPage(
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    val viewModel: NewsHomeReaderViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    val uriHandler = LocalUriHandler.current

    PageFrame() { isLandscape ->
        if (state.isShowInfos) {
            InfoPage(onAction)
        } else if (state.isEditingSettings) {
            SettingsPage(onAction)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))

                    IndicatorButton(
                        modifier = Modifier,
                        width = 50.dp,
                        height = 50.dp,
                        leadingIcon = painterResource(Res.drawable.icon_info_24px)
                    ) {
                        onAction(NewsHomeReaderAction.OnShowInfosClick(!state.isShowInfos))
                    }

                    IndicatorButton(
                        modifier = Modifier,
                        width = 50.dp,
                        height = 50.dp,
                        leadingIcon = painterResource(Res.drawable.icon_settings_24px),
                    ) {
                        onAction(NewsHomeReaderAction.OnEditSettingsClick(!state.isEditingSettings))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                ) {
                    ErrorCard(
                        errorMessage = state.uiMessage,
                        severity = state.uiMessageSeverity,
                        shapeContainer = MaterialTheme.shapes.small
                    )

                    if (state.currentNewsArticle != null) {
                        state.currentNewsItem?.let { ni ->
                            NewsArticleCard(
                                isLandscape = isLandscape,
                                uriHandler = uriHandler,
                                newsItem = ni,
                                onAction = onAction,
                                connectivityManager = connectivityManager
                            )
                        }
                    } else {
                        NewsFeeds(
                            isLandscape = isLandscape,
                            onAction = onAction,
                            connectivityManager = connectivityManager
                        )
                    }
                }
            }
        }
    }
}


