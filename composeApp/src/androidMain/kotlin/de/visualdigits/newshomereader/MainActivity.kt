package de.visualdigits.newshomereader

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.domain.model.platform.PlatformType
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val scheduler: FeedScheduler by inject()

    private val viewModel: NewsHomeReaderViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = window.decorView
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    val isDark = state.settings?.get<DisplayThemeEnum>(SK.displayTheme)?.isDark?:false

                    // for newer device starting with android 14/15
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            lightScrim = Color.TRANSPARENT,
                            darkScrim = Color.TRANSPARENT,
                            detectDarkMode = { isDark }
                        )
                    )

                    // for older devices down to android 6
                    val olderWindow = this@MainActivity.window
                    WindowCompat.getInsetsController(olderWindow, view).apply {
                        isAppearanceLightStatusBars = !isDark
                    }
                }
            }
        }


        lifecycleScope.launch {
            combine(
                viewModel.state
                    .map { it.settings?.get<RefreshIntervalEnum>(SK.refreshInterval)?.longValue }
                    .distinctUntilChanged(),
                viewModel.state
                    .map { it.maxImageSize }
                    .distinctUntilChanged() // Verhindert unnötige Trigger bei gleichem Wert
                    .filterNotNull()
            ) { interval, maxImageSize ->
                interval to maxImageSize
            }.collect { (interval, maxImageSize) ->
                scheduler.scheduleEvery(interval ?: 60, maxImageSize)
            }
        }

        setContent {
            App(PlatformType.android)
        }
    }
}
