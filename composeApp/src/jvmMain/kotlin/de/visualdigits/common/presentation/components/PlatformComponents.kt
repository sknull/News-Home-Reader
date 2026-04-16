package de.visualdigits.common.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.visualdigits.common.domain.model.UiPlatform

@Composable
actual fun androidPlatform(): UiPlatform = UiPlatform.NONE

@Composable
actual fun Modifier.platformFocus(onClick: (() -> Unit)?): Modifier = this
