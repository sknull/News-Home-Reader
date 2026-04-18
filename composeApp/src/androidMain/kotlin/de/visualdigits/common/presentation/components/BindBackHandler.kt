package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable

@Composable
actual fun BindBackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(isEnabled, onBack)
}
