package de.visualdigits.newshomereader.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.visualdigits.newshomereader.data.repository.ConnectivityManager
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderViewModel
import de.visualdigits.newshomereader.presentation.screen.page.MainPage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreenRoot(
    viewModel: NewsHomeReaderViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val connectivityManager = koinInject<ConnectivityManager>()

    MainScreen(
        onAction = { action ->
            viewModel.onAction(action)
        },
        connectivityManager = connectivityManager
    )
}

@Composable
fun MainScreen(
    onAction: (NewsHomeReaderAction) -> Unit,
    connectivityManager: ConnectivityManager
) {
    MainPage(
        onAction = { action ->
            onAction(action)
        },
        connectivityManager = connectivityManager
    )
}
