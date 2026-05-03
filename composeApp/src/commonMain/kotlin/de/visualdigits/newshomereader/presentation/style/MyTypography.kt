package de.visualdigits.newshomereader.presentation.style

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.Roboto_Black
import de.visualdigits.compose.resources.Roboto_Bold
import de.visualdigits.compose.resources.Roboto_Regular
import org.jetbrains.compose.resources.Font

@Composable
fun typography(
    textColor: Color,
    sizeFactor: Float
): Typography {
    val fontFamilyRegular = FontFamily(Font(Res.font.Roboto_Regular))
    val fontFamilyBold = FontFamily(Font(Res.font.Roboto_Bold))
    val fontFamilyBlack = FontFamily(Font(Res.font.Roboto_Black))
    return Typography(
        headlineSmall = TextStyle(
            fontFamily = fontFamilyBlack,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.5.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamilyBlack,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.5.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamilyBlack,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp * sizeFactor,
            lineHeight = 1.5.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        titleSmall = TextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        bodySmall = TextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        displaySmall = TextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        displayLarge = TextStyle(
            fontFamily = fontFamilyRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),

        labelSmall = TextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamilyBold,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp * sizeFactor,
            lineHeight = 1.2.em,
            letterSpacing = 0.2.sp,
            color = textColor
        )

    )
}
