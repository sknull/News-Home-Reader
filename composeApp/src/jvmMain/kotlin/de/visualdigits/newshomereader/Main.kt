package de.visualdigits.newshomereader

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.common.domain.service.getPlatformLogWriters
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.di.initKoin
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Window
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {

    val koinApp = initKoin()

    val viewModel: NewsHomeReaderViewModel = koinApp.koin.get()
    val scheduler: FeedScheduler = koinApp.koin.get()

    CoroutineScope(Dispatchers.Default).launch {
        viewModel.state
            .map { it.settings?.get<RefreshIntervalEnum>(SK.refreshInterval)?.longValue }
            .distinctUntilChanged()
            .collect { interval ->
            scheduler.scheduleEvery(interval?:60)
        }
    }

    val writers = getPlatformLogWriters()
    Logger.setLogWriters(writers)
    Logger.setTag("NewsHomeReader")

    System.setProperty("flatlaf.useWindowDecorations", "true")

    application {
        val ioScope = rememberCoroutineScope()
        val state = rememberWindowState(
            width = 1200.dp,
            height = 900.dp,
            position = WindowPosition(Alignment.Center)
        )

        LaunchedEffect(viewModel) {
            viewModel.state.collect { state ->
                val displayTheme = state.settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: DisplayThemeEnum.LIGHT
                withContext(Dispatchers.Main) {
                    try {
                        UIManager.setLookAndFeel(displayTheme.laf)
                        SwingUtilities.invokeLater {
                            for (window in Window.getWindows()) {
                                SwingUtilities.updateComponentTreeUI(window)
                            }
                        }
                    } catch (e: Exception) {
                        Logger.e("Laf konnte nicht gesetzt werden", e)
                    }
                }
            }
        }

        Window(
            onCloseRequest = {
                ioScope.cancel("Normal Exit")
                exitApplication()
            },
            title = "News Home Reader",
//            icon = painterResource(Res.drawable.Msfs2024Tools),
            state = state
        ) {
            App()
        }
    }
}
