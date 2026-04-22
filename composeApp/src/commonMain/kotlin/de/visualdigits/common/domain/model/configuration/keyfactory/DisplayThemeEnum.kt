package de.visualdigits.common.domain.model.configuration.keyfactory

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.presentation.components.StudioClockColors
import de.visualdigits.common.presentation.components.defaultStudioClockColors
import org.jetbrains.compose.resources.DrawableResource

enum class DisplayThemeEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val colorScheme: ColorScheme,
    val textLinkStyles:TextLinkStyles,
    val textColor: Color,
    val studioClockColors: StudioClockColors,
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
        colorScheme = darkColorScheme(
            primary = Color(0xFF3C3F40),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE67A2A),
            onPrimaryContainer = Color(0xFFFFFFFF),

            secondary = Color(0xFF313030),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFE1E1E1),
            onSecondaryContainer = Color(0xFF9A9A9A),

            tertiary = Color(0xFF5A361B),
            tertiaryContainer = Color(0xFFA5612F),

            background = Color(0xFF3C3F40),
            onBackground = Color(0xFFFFFFFF),

            surface = Color.Transparent,
            onSurface = Color(0xFFE67A2A),
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
            outlineVariant = Color(0xFFE67A2A),

            primaryFixed = Color(0xAA000000)
        ),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xFFE67A2A),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = StudioClockColors(
            colorHours = Color(0xFFC96012),
            colorMinutes = Color(0xFFDCA175),
            colorSeconds = Color(0xFFC96012),
            colorTime = Color(0xFFC96012),
            colorDate = Color(0xFFDCA175),
            colorBackground = Color(0xdd000000),
        )
    ),

    DARK(
        isDark = true,
        uiText = UiText.DynamicString("Dark"),
        drawableResourceId = null,
        colorScheme = darkColorScheme(
            primary = Color(0xFF000000),
            onPrimary = Color(0xff000000),
            primaryContainer = Color(0xff3b84eb),
            onPrimaryContainer = Color(0xFFFFFFFF),

            secondary = Color(0xFF313030),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFE1E1E1),
            onSecondaryContainer = Color(0xFF9A9A9A),

            tertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFFFFF),

            background = Color(0xFF000000),
            onBackground = Color(0xFFFFFFFF),

            surface = Color.Transparent,
            onSurface = Color(0xFFFFFFFF),
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
            outlineVariant = Color(0xff3b84eb),

            primaryFixed = Color(0xAA000000)
        ),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xff3b84eb),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFFFFFFFF),
        studioClockColors = defaultStudioClockColors
    ),

    LIGHT(
        isDark = false,
        uiText = UiText.DynamicString("Light"),
        drawableResourceId = null,
        colorScheme = lightColorScheme(
            primary = Color(0xFFFFFFFF),
            onPrimary = Color(0xff000000),
            primaryContainer = Color(0xff3b84eb),
            onPrimaryContainer = Color(0xFFFFFFFF),

            secondary = Color(0xFFBFBEBE),
            onSecondary = Color(0xFF000000),

            secondaryContainer = Color(0xFFE1E1E1),
            onSecondaryContainer = Color(0xFF9A9A9A),

            tertiary = Color(0xFF000000),
            tertiaryContainer = Color(0xFF000000),

            background = Color(0xFFFFFFFF),
            onBackground = Color(0xFF000000),

            surface = Color.Transparent,
            onSurface = Color(0xFF000000),
            inverseSurface = Color(0xFFFFFFFF),
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
            outlineVariant = Color(0xff3b84eb),

            primaryFixed = Color(0xAA000000)
        ),
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = Color(0xff3b84eb),
                textDecoration = TextDecoration.Underline
            )
        ),
        textColor = Color(0xFF000000),
        studioClockColors = defaultStudioClockColors
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
