package de.visualdigits.newshomereader.presentation.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_article_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.compose.resources.icon_image_24px
import de.visualdigits.compose.resources.icon_info_24px
import de.visualdigits.compose.resources.icon_menu_24px
import de.visualdigits.compose.resources.icon_refresh_24px
import de.visualdigits.compose.resources.icon_settings_24px
import de.visualdigits.compose.resources.tooltip_refresh_newsfeed
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.type.ProgressStage
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainMenuBar(
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
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
            leadingIcon = painterResource(Res.drawable.icon_menu_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface
        ) {
            val isExpanded = state.collapsibleState["group_newsfeeds_navigation"] == true
            onAction(NewsHomeReaderAction.OnCollapsibleStateChange("group_newsfeeds_navigation", !isExpanded))
        }

        IndicatorButton(
            modifier = Modifier,
            width = 30.dp,
            height = 30.dp,
            padding = 2.dp,
            leadingIcon = painterResource(Res.drawable.icon_edit_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onAction(NewsHomeReaderAction.OnEditModeClick(!state.isEditMode))
        }

        Spacer(Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { state.currentProgress },
                modifier = Modifier
                    .size(24.dp),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                trackColor = MaterialTheme.colorScheme.surfaceDim,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
            )

            when (state.progressStage) {
                ProgressStage.LOAD_ARTICLES -> Icon(
                    modifier = Modifier
                        .size(14.dp),
                    painter = painterResource(Res.drawable.icon_article_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inverseSurface,
                )
                ProgressStage.LOAD_IMAGES -> Icon(
                    modifier = Modifier
                        .size(14.dp),
                    painter = painterResource(Res.drawable.icon_image_24px),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inverseSurface
                )
                else -> {}
            }
        }

        val wifiOnly = state.settings?.get<BooleanEnum>(SK.refreshWifiOnly)?.booleanValue ?: false
        IndicatorButton(
            modifier = Modifier,
            enabled = !wifiOnly || connectivityManager.connectivityMode().isFreeOfCharge,
            width = 30.dp,
            height = 30.dp,
            padding = 2.dp,
            leadingIcon = painterResource(Res.drawable.icon_refresh_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface,
            toolTip = stringResource(Res.string.tooltip_refresh_newsfeed),
        ) {
            onAction(NewsHomeReaderAction.OnNewsFeedsRefresh())
        }

        IndicatorButton(
            modifier = Modifier,
            width = 30.dp,
            height = 30.dp,
            padding = 2.dp,
            leadingIcon = painterResource(Res.drawable.icon_info_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onAction(NewsHomeReaderAction.OnShowInfosClick(!state.isShowInfos))
        }

        IndicatorButton(
            modifier = Modifier,
            width = 30.dp,
            height = 30.dp,
            leadingIcon = painterResource(Res.drawable.icon_settings_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onAction(NewsHomeReaderAction.OnEditSettingsClick(!state.isEditingSettings))
        }
    }
}
