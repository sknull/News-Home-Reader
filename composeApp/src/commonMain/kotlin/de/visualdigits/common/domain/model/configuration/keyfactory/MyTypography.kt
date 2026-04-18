package de.visualdigits.common.domain.model.configuration.keyfactory

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp


fun typography(
    textColor: Color,
    sizeFactor: Float
): Typography {
    return Typography(
        headlineSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.5.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.5.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp * sizeFactor,
            lineHeight = 1.5.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        titleSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        bodySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        displaySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        displayLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        labelSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        )

    )
}
