package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.typography
import de.visualdigits.common.presentation.components.BindBackHandler
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.components.container.ErrorCard
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.compose.resources.icon_info_24px
import de.visualdigits.compose.resources.icon_menu_24px
import de.visualdigits.compose.resources.icon_refresh_24px
import de.visualdigits.compose.resources.icon_settings_24px
import de.visualdigits.compose.resources.title_add_newsfeedconfiguration
import de.visualdigits.compose.resources.title_edit_newsfeedconfiguration
import de.visualdigits.compose.resources.tooltip_refresh_newsfeed
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsArticleCard
import de.visualdigits.newshomereader.presentation.screen.page.newstab.NewsFeeds
import de.visualdigits.newshomereader.presentation.style.MyShapes
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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

    val focusRequester = remember { FocusRequester() }
    var currentNewsFeedGroupName by remember { mutableStateOf<String>("") }

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
                if (state.isShowInfos) {
                    InfoPage(uriHandler, onAction)
                } else if (state.isEditingSettings) {
                    SettingsPage(state, viewModel.scrollPosition, onAction)
                } else if (state.isAddingNewsFeedConfiguration || state.isEditingNewsFeedConfiguration) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = if (state.isEditingNewsFeedConfiguration) stringResource(Res.string.title_edit_newsfeedconfiguration) else  stringResource(Res.string.title_add_newsfeedconfiguration),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(Modifier.height(16.dp))

                        ConfigurationEditForm(
                            scrollPosition = viewModel.scrollPosition,
                            scrollbarId = "configuration_settings",
                            fieldHeight = 50.dp,
                            onValueChange = { keyValue ->
                                onAction(
                                    NewsHomeReaderAction.OnNewsFeedConfigurationValueChanged(
                                        newsFeedConfiguration = state.editedNewsFeedConfiguration,
                                        keyValue = keyValue
                                    )
                                )
                            },
                            configuration = state.editedNewsFeedConfiguration,
                            onCancelClick = {
                                onAction(
                                    if (state.isAddingNewsFeedConfiguration) {
                                        NewsHomeReaderAction.OnAddNewsFeedConfigurationCancelClick()
                                    } else {
                                        NewsHomeReaderAction.OnEditNewsFeedConfigurationCancelClick()
                                    }
                                )
                            },
                            onOkClick = {
                                onAction(
                                    if (state.isAddingNewsFeedConfiguration) {
                                        NewsHomeReaderAction.OnAddNewsFeedConfigurationOkClick(state.editedNewsFeedConfiguration)
                                    } else {
                                        NewsHomeReaderAction.OnEditNewsFeedConfigurationOkClick(state.editedNewsFeedConfiguration)
                                    }
                                )
                            },
                            onAction = onAction
                        )
                    }
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
                            IndicatorButton(
                                modifier = Modifier,
                                width = 30.dp,
                                height = 30.dp,
                                padding = 2.dp,
                                leadingIcon = painterResource(Res.drawable.icon_menu_24px)
                            ) {
                                val isExpanded = state.collapsibleState["group_newsfeeds_navigation"] == true
                                onAction(NewsHomeReaderAction.OnCollapsibleStateChange("group_newsfeeds_navigation", !isExpanded))
                            }

                            IndicatorButton(
                                modifier = Modifier,
                                width = 30.dp,
                                height = 30.dp,
                                padding = 2.dp,
                                leadingIcon = painterResource(Res.drawable.icon_edit_24px)
                            ) {
                                onAction(NewsHomeReaderAction.OnEditModeClick(!state.isEditMode))
                            }

                            Spacer(Modifier.weight(1f))

                            if (state.currentProgress > 0.0f) {
                                val animatedProgress by animateFloatAsState(
                                    targetValue = state.currentProgress,
                                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec // Sorgt für sanftes Gleiten
                                )
                                CircularProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier
                                        .size(24.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                                    trackColor = MaterialTheme.colorScheme.surfaceDim,
                                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                                )
                            }

                            val wifiOnly = state.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
                            IndicatorButton(
                                modifier = Modifier,
                                enabled = !wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge,
                                width = 30.dp,
                                height = 30.dp,
                                padding = 2.dp,
                                leadingIcon = painterResource(Res.drawable.icon_refresh_24px),
                                toolTip = stringResource(Res.string.tooltip_refresh_newsfeed),
                            ) {
                                onAction(NewsHomeReaderAction.OnNewsFeedsRefresh())
                            }

                            IndicatorButton(
                                modifier = Modifier,
                                width = 30.dp,
                                height = 30.dp,
                                padding = 2.dp,
                                leadingIcon = painterResource(Res.drawable.icon_info_24px)
                            ) {
                                onAction(NewsHomeReaderAction.OnShowInfosClick(!state.isShowInfos))
                            }

                            IndicatorButton(
                                modifier = Modifier,
                                width = 30.dp,
                                height = 30.dp,
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
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        NewsArticleCard(
                                            modifier = Modifier
                                                .widthIn(max = 1000.dp),
                                            scrollPosition = viewModel.scrollPosition,
                                            maxWidth = mw,
                                            maxImageSize = maxImageSize,
                                            newsArticle = state.currentNewsArticle,
                                            settings = state.settings,
                                            uriHandler = uriHandler,
                                            newsItem = ni,
                                            onAction = onAction,
                                            connectivityManager = connectivityManager
                                        )
                                    }
                                }
                            } else {
                                NewsFeeds(
                                    state = state,
                                    scrollPosition = viewModel.scrollPosition,
                                    displayTheme = displayTheme,
                                    maxWidth = mw,
                                    maxImageSize = maxImageSize,
                                    settings = state.settings,
                                    uriHandler = uriHandler,
                                    onAction = onAction,
                                    connectivityManager = connectivityManager
                                )
                            }
                        }
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


