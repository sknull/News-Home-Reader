package de.visualdigits.common.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.domain.model.UiPlatform
import java.io.File
import java.io.InputStream

@Composable
expect fun PlatformVerticalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    interactionSource: MutableInteractionSource
)

@Composable
expect fun PlatformLazyVerticalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: LazyListState,
    interactionSource: MutableInteractionSource
)

@Composable
expect fun PlatformToolTip(
    text: String?,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    modifier: Modifier = Modifier,
    shadowSize: Dp = 5.dp,
    content: @Composable () -> Unit
)

@Composable
expect fun androidPlatform(): UiPlatform

@Composable
expect fun Modifier.platformFocus(
    onClick: (() -> Unit)? = null
): Modifier


@Composable
expect fun PlatformFileChooser(
    label: String,
    title: String,
    fileMode: FileMode,
    startDirectory: File? = null,
    options: List<String> = listOf(),
    buttonShape: Shape = MaterialTheme.shapes.extraSmall,
    buttonColor: Color = MaterialTheme.colorScheme.surface,
    onCancel: (() -> Unit)? = null,
    onOk: (InputStream) -> Unit
)
