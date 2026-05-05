package de.visualdigits.newshomereader

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.domain.service.getPlatformLogWriters
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.favicon
import de.visualdigits.newshomereader.data.repository.FeedScheduler
import de.visualdigits.newshomereader.di.platformModule
import de.visualdigits.newshomereader.di.sharedModule
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import java.awt.Window
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {

    val koinApp = startKoin {
        modules(sharedModule, platformModule)
    }
    val viewModel: NewsHomeReaderViewModel = koinApp.koin.get()
    val scheduler: FeedScheduler = koinApp.koin.get()
    val homeDirectoryPath = koinApp.koin.get<String>(named("homeDirectory"))

    CoroutineScope(Dispatchers.Default).launch {
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

    val writers = getPlatformLogWriters(homeDirectoryPath, "NewsHomeReader.log")
    Logger.setLogWriters(writers)
    Logger.setTag("NewsHomeReader")
    Logger.setMinSeverity(Severity.Info)

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
            icon = painterResource(Res.drawable.favicon),
            state = state
        ) {
            App(PlatformType.jvm)
        }
    }
}
