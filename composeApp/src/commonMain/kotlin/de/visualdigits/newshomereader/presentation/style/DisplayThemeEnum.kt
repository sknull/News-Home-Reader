package de.visualdigits.newshomereader.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import de.visualdigits.common.presentation.model.PlatformScrollbarStyle
import org.jetbrains.compose.resources.DrawableResource

enum class DisplayThemeEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val textColor: Color,
    val backgroundImage: Painter? = null,
    val isDark: Boolean // consider as dark theme for android
) : StringResourceEnumerable<DisplayThemeEnum> {

    //
    // Remember to configure the fitting laf in
    // de/visualdigits/newshomereader/DisplayThemeEnumExtensions.kt
    //

    ANTHRACITE(
        isDark = true,
        uiText = UiText.DynamicString("Anthracite"),
        drawableResourceId = null,
        textColor = Color(0xFFFFFFFF),
    ),

    LIGHT(
        isDark = false,
        uiText = UiText.DynamicString("Light"),
        drawableResourceId = null,
        textColor = Color(0xFF000000),
    )
    ;

    override fun toString(): String = name

    companion object : KeyFactory<DisplayThemeEnum> {

        val SPOT_COLOR_DEFAULT: HsvColor = HsvColor.fromComposeColor(Color(0xFF439DDE))

        override val options: List<Triple<DisplayThemeEnum, UiText?, DrawableResource?>> = entries.map { e -> Triple(e, e.uiText, e.drawableResourceId) }

        override fun fromString(value: String?): DisplayThemeEnum? {
            return entries.find { e -> e.name == value }
        }

        override fun fromValue(value: Any?): DisplayThemeEnum? {
            return when (value) {
                is String -> fromString(value)
                is DisplayThemeEnum -> value
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            return (value as? DisplayThemeEnum)?.name?:value?.toString()
        }
    }
}

fun textLinkStyles(spotColor: HsvColor): TextLinkStyles = TextLinkStyles(
    style = SpanStyle(
        color = spotColor.toComposeColor(),
        textDecoration = TextDecoration.Underline
    )
)

fun lightTheme(spotColor: HsvColor): ColorScheme = lightColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xff000000),
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = Color(0xFFBFBEBE),
    onSecondary = Color(0xFF000000),

    secondaryContainer = Color(0xFFE1E1E1),
    onSecondaryContainer = Color(0xFF9A9A9A),

    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),

    surface = Color.Transparent,
    onSurface = spotColor.toComposeColor(), // deco color
    primaryFixedDim = Color(0xFFBCBCBC),

    inverseSurface = Color(0xFF000000),
    surfaceContainer = Color(0xFF000000),
    surfaceContainerHigh = Color.Transparent,
    surfaceContainerLow = Color.Transparent,
    surfaceContainerLowest = Color(0xFFD8D8D8),
    surfaceDim = Color(0xffaaaaaa),

    error = Color(0xffff002a),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xffff002a),
    onErrorContainer = Color(0xFFFFFFFF),

    outline = Color(0xFF000000),

    primaryFixed = Color(0xAA000000)
)

fun anthraciteTheme(spotColor: HsvColor): ColorScheme = darkColorScheme(
    primary = Color(0xFF3C3F40),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = Color(0xFF313030),
    onSecondary = Color(0xFFFFFFFF),

    secondaryContainer = Color(0xFFE1E1E1),
    onSecondaryContainer = Color(0xFF9A9A9A),

    background = Color(0xFF3C3F40),
    onBackground = Color(0xFFFFFFFF),

    surface = Color.Transparent,
    onSurface = spotColor.toComposeColor(), // spot color
    primaryFixedDim = Color(0x77000000),

    inverseSurface = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color.Transparent,
    surfaceContainerLow = Color.Transparent,
    surfaceContainerLowest = Color(0xFF373737),
    surfaceDim = Color(0xFF393939),

    error = Color(0xffff002a),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xffff002a),
    onErrorContainer = Color(0xFFFFFFFF),

    outline = Color(0xFFFFFFFF),

    primaryFixed = Color(0xAA000000),
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
