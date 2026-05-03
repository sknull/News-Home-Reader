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
import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import de.visualdigits.common.domain.util.copy
import de.visualdigits.common.presentation.components.StudioClockColors
import de.visualdigits.common.presentation.components.defaultStudioClockColors
import de.visualdigits.common.presentation.model.PlatformScrollbarStyle
import org.jetbrains.compose.resources.DrawableResource

enum class DisplayThemeEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val colorScheme: ColorScheme,
    val textLinkStyles: TextLinkStyles,
    val textColor: Color,
    val studioClockColors: StudioClockColors,
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
        colorScheme = createAnthraciteTheme(spotColor = Color(0xFF868686)),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFF868686),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(spotColor = Color(0xFF868686)),
    ),

    ANTHRACITE_ORANGE(
        isDark = true,
        uiText = UiText.DynamicString("Anthracite Orange"),
        drawableResourceId = null,
        colorScheme = createAnthraciteTheme(spotColor = Color(0xFFE67A2A)),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFFE67A2A),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(spotColor = Color(0xFFE67A2A)),
    ),

    ANTHRACITE_BLUE(
        isDark = true,
        uiText = UiText.DynamicString("Anthracite Blue"),
        drawableResourceId = null,
        colorScheme = createAnthraciteTheme(spotColor = Color(0xFF439DDE)),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFF439DDE),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(spotColor = Color(0xFF439DDE)),
    ),

    ANTHRACITE_GREEN(
        isDark = true,
        uiText = UiText.DynamicString("Anthracite Green"),
        drawableResourceId = null,
        colorScheme = createAnthraciteTheme(spotColor = Color(0xFF43DE58)),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFF43DE58),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(spotColor = Color(0xFF43DE58)),
    ),

    ANTHRACITE_PURPLE(
        isDark = true,
        uiText = UiText.DynamicString("Anthracite Purple"),
        drawableResourceId = null,
        colorScheme = createAnthraciteTheme(spotColor = Color(0xFF7E43DE)),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFF7E43DE),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(spotColor = Color(0xFF7E43DE)),
    ),

    ANTHRACITE_YELLOW(
        isDark = true,
        uiText = UiText.DynamicString("Anthracite Yellow"),
        drawableResourceId = null,
        colorScheme = createAnthraciteTheme(spotColor = Color(0xFFFCD03E)),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFFFCD03E),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(spotColor = Color(0xFFFCD03E)),
    ),

    DARK(
        isDark = true,
        uiText = UiText.DynamicString("Dark"),
        drawableResourceId = null,
        colorScheme = darkColorScheme(
            primary = Color(0xFF000000),
            onPrimary = Color(0xff000000),
            onPrimaryContainer = Color(0xFFFFFFFF),

            secondary = Color(0xFF313030),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFE1E1E1),
            onSecondaryContainer = Color(0xFF9A9A9A),

            background = Color(0xFF000000),
            onBackground = Color(0xFFFFFFFF),

            surface = Color.Transparent,
            onSurface = Color(0xFF1B9EFF), // deco color

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

            primaryFixed = Color(0xAA000000)
        ),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xff3b84eb),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = createStudioClockColors(Color(0xff3b84eb))
    ),

    LIGHT(
        isDark = false,
        uiText = UiText.DynamicString("Light"),
        drawableResourceId = null,
        colorScheme = lightColorScheme(
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
            onSurface = Color(0xFF1B9EFF), // deco color

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
        ),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xff3b84eb),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFF000000),
        studioClockColors = createStudioClockColors(Color(0xff3b84eb))
    )
    ;

    override fun toString(): String = name.lowercase()

    companion object : KeyFactory<DisplayThemeEnum> {

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

private fun createAnthraciteTheme(spotColor: Color): ColorScheme = darkColorScheme(
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
    onSurface = spotColor, // deco color

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

private fun createStudioClockColors(spotColor: Color): StudioClockColors = StudioClockColors(
    colorHours = spotColor,
    colorMinutes = spotColor.copy(saturation = 0.3f, value = 0.8f),
    colorSeconds = spotColor,
    colorTime = spotColor,
    colorDate = spotColor.copy(saturation = 0.3f, value = 0.8f),
    colorBackground = Color(0xdd000000),
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
