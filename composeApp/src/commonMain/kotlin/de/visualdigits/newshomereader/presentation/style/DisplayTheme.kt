package de.visualdigits.newshomereader.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.presentation.model.PlatformScrollbarStyle

val BACKGROUND_COLOR_DEFAULT: HsvColor = HsvColor.fromComposeColor(Color(0xFFFFFFFF))
val BUTTON_COLOR_DEFAULT: HsvColor = HsvColor.fromComposeColor(Color(0xFF888888))
val TEXT_COLOR_DEFAULT: HsvColor = HsvColor.fromComposeColor(Color(0xFF000000))
val SPOT_COLOR_DEFAULT: HsvColor = HsvColor.fromComposeColor(Color(0xFF439DDE))

fun textLinkStyles(spotColor: HsvColor): TextLinkStyles = TextLinkStyles(
    style = SpanStyle(
        color = spotColor.toComposeColor(),
        textDecoration = TextDecoration.Underline
    )
)

fun theme(
    backgroundColor: HsvColor,
    textColor: HsvColor,
    spotColor: HsvColor
): ColorScheme = lightColorScheme(
    secondary = Color(0xFFE1E1E1), // switchbox unchecked track
    onSecondary = Color(0xFF9A9A9A), // switchbox unchecked thumb and border

    background = backgroundColor.toComposeColor(),
    onBackground = textColor.toComposeColor(),

    surface = Color.Transparent, // buttons
    onSurface = spotColor.toComposeColor(), // spot color

    surfaceContainer = Color.Transparent,
    surfaceContainerLowest = Color(0xFF797979),

    errorContainer = Color(0xffff002a), // delete dialogs
    onErrorContainer = Color(0xFFFFFFFF), // delete dialogs

    outline = textColor.toComposeColor(), // focused border

    primaryFixed = Color(0xAA000000) // terminal
)

@Composable
fun scrollbarStyle() = PlatformScrollbarStyle(
    minimalHeight = 16.dp,
    thickness = 8.dp,
    shape = RoundedCornerShape(4.dp),
    hoverDurationMillis = 300,
    unhoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
)
