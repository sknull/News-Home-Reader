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
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import java.io.InputStream
import java.io.OutputStream

@Composable
expect fun BindBackHandler(isEnabled: Boolean, onBack: () -> Unit)

@Composable
expect fun PlatformVerticalScrollbarBox(
    boxModifier: Modifier = Modifier,
    scrollbarModifier: Modifier = Modifier,
    scrollbarId: String,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    onAction: (NewsHomeReaderAction) -> Unit,
    rows: () -> List<Pair<String, @Composable () -> Unit>>
)

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
    buttonTextStyle: TextStyle,
    title: String,
    fileMode: FileMode,
    options: List<String> = listOf(),
    buttonShape: Shape = MaterialTheme.shapes.extraSmall,
    buttonColor: Color = MaterialTheme.colorScheme.surface,
    buttonWidth: Dp = 120.dp,
    buttonHeight: Dp = 50.dp,
    onCancel: (() -> Unit)? = null,
    onOk: (String, InputStream) -> Unit
)

@Composable
expect fun PlatformFileSaver(
    label: String,
    buttonTextStyle: TextStyle,
    title: String,
    fileMode: FileMode,
    suggestedFileName: String,
    buttonShape: Shape = MaterialTheme.shapes.extraSmall,
    buttonColor: Color = MaterialTheme.colorScheme.surface,
    buttonWidth: Dp = 120.dp,
    buttonHeight: Dp = 50.dp,
    onCancel: (() -> Unit)? = null,
    onOk: (String, OutputStream) -> Unit
)
