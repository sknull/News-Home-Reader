package de.visualdigits.newshomereader

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import coil3.compose.setSingletonImageLoaderFactory
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.ConnectivityManager
import de.visualdigits.newshomereader.data.repository.ImageCache
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
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

    viewModel.platformType = platformType
    val connectivityManager = koinInject<ConnectivityManager>()

    _root_ide_package_.de.visualdigits.newshomereader.presentation.page.MainPage(
        viewModel = viewModel,
        connectivityManager = connectivityManager
    )
}
