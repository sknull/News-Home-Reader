package de.visualdigits.newshomereader

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import coil3.compose.setSingletonImageLoaderFactory
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.data.repository.ImageCache
import de.visualdigits.newshomereader.domain.model.platform.PlatformType
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.page.MainPage
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

    MainPage(
        viewModel = viewModel,
        connectivityManager = connectivityManager
    )
}
