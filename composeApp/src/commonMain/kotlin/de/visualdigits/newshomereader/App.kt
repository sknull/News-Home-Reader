package de.visualdigits.newshomereader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.setSingletonImageLoaderFactory
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.newshomereader.data.repository.ImageCache
import de.visualdigits.newshomereader.domain.model.platform.PlatformType
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.MainScreenRoot
import de.visualdigits.newshomereader.presentation.style.MyShapes
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(platformType: PlatformType) {

    val imageCache = koinInject<ImageCache>()
    val viewModel = koinViewModel<NewsHomeReaderViewModel>()

    setSingletonImageLoaderFactory { context ->
        imageCache.getImageLoader()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val displayTheme = state.settings?.get<DisplayThemeEnum>(SK.displayTheme) ?: DisplayThemeEnum.LIGHT
    viewModel.platformType = platformType

    MaterialTheme(
        colorScheme = displayTheme.colorScheme,
        typography = displayTheme.typography,
        shapes = MyShapes
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(displayTheme.colorScheme.background)
                .safeDrawingPadding()
        ) {
            MainScreenRoot(
                viewModel = viewModel
            )
        }
    }
}
