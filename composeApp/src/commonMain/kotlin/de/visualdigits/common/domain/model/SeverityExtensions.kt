package de.visualdigits.common.domain.model

import androidx.compose.ui.graphics.Color
import co.touchlab.kermit.Severity

fun Severity.color(): Color {
    return when (this) {
        Severity.Info -> Color(0xff00ff00)
        Severity.Warn -> Color(0xffffff00)
        Severity.Error -> Color(0xffff0000)
        Severity.Verbose -> Color(0xff666666)
        Severity.Debug -> Color(0xffaaaaaa)
        Severity.Assert -> Color(0xffff7700)
    }
}
