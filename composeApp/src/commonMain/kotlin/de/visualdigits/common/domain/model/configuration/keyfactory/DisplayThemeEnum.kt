package de.visualdigits.common.domain.model.configuration.keyfactory

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import org.jetbrains.compose.resources.DrawableResource

enum class DisplayThemeEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val colorScheme: ColorScheme,
    val textLinkStyles:TextLinkStyles,
    val typography: Typography
) : StringResourceEnumerable<DisplayThemeEnum> {

    //
    // Remember to configure the fitting laf in
    // de/visualdigits/newshomereader/DisplayThemeEnumExtensions.kt
    //

    DARK(
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
        typography = myTypography(Color.White)
    ),

    LIGHT(
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
        typography = myTypography(Color.Black)
    ),
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

private fun myTypography(
    textColor: Color
): Typography {
    return Typography(
        headlineSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            lineHeight = 18.sp * 1.1,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            lineHeight = 24.sp * 1.1,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            lineHeight = 30.sp * 1.1,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        titleSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 14.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 24.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        bodySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 14.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 18.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 24.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        displaySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 14.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 18.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        displayLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 24.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        labelSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 14.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 18.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 24.sp * 1.2,
            letterSpacing = 0.2.sp,
            color = textColor
        )

    )
}
