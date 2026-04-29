package de.visualdigits.newshomereader.presentation.screen.page.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import de.visualdigits.common.presentation.components.StudioClock
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.digital_dream_skew_fat
import org.jetbrains.compose.resources.Font

@Composable
fun StudioClockTab() {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StudioClock(
            modifier = Modifier
                .fillMaxSize(),
            fontFamily = FontFamily(Font(Res.font.digital_dream_skew_fat))
        )
    }
}
