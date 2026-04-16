package de.visualdigits.common.presentation.components

import android.app.UiModeManager
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_MASK
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_UNDEFINED
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_APPLIANCE
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_CAR
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_DESK
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_MASK
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_TELEVISION
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_UNDEFINED
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_VR_HEADSET
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_WATCH
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.UiPlatform

@Composable
actual fun androidPlatform(): UiPlatform {
    val context = LocalContext.current
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return when (uiModeManager.currentModeType) {
        UI_MODE_TYPE_MASK -> UiPlatform.UI_MODE_TYPE_MASK
        UI_MODE_TYPE_UNDEFINED -> UiPlatform.UI_MODE_TYPE_UNDEFINED
        UI_MODE_TYPE_NORMAL -> UiPlatform.UI_MODE_TYPE_NORMAL
        UI_MODE_TYPE_DESK -> UiPlatform.UI_MODE_TYPE_DESK
        UI_MODE_TYPE_CAR -> UiPlatform.UI_MODE_TYPE_CAR
        UI_MODE_TYPE_TELEVISION -> UiPlatform.UI_MODE_TYPE_TELEVISION
        UI_MODE_TYPE_APPLIANCE -> UiPlatform.UI_MODE_TYPE_APPLIANCE
        UI_MODE_TYPE_WATCH -> UiPlatform.UI_MODE_TYPE_WATCH
        UI_MODE_TYPE_VR_HEADSET -> UiPlatform.UI_MODE_TYPE_VR_HEADSET
        UI_MODE_NIGHT_MASK -> UiPlatform.UI_MODE_NIGHT_MASK
        UI_MODE_NIGHT_UNDEFINED -> UiPlatform.UI_MODE_NIGHT_UNDEFINED
        UI_MODE_NIGHT_NO -> UiPlatform.UI_MODE_NIGHT_NO
        UI_MODE_NIGHT_YES -> UiPlatform.UI_MODE_NIGHT_YES
        else -> UiPlatform.NONE
    }
}

@Composable
actual fun Modifier.platformFocus(onClick: (() -> Unit)?): Modifier {
    val platform = androidPlatform()
    var isFocused by remember { mutableStateOf(false) }

    return if(platform == UiPlatform.UI_MODE_TYPE_TELEVISION) {
         this
             .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .focusable()
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
            )
    } else {
        if (onClick != null) this.clickable { onClick() } else this
    }
}
