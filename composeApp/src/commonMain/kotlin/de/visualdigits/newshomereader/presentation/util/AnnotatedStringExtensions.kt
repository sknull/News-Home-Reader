package de.visualdigits.newshomereader.presentation.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

fun AnnotatedString.highlightQuery(
    query: String,
    foregroundColor: Color = Color.Black,
    backgroundColor: Color = Color.Yellow
): AnnotatedString {
    return if (query.isNotBlank()) {
        buildAnnotatedString {
            append(this@highlightQuery) // Originalen Content (mit deinem HTML-Styling) übernehmen

            val text = this@highlightQuery.text
            var startIndex = text.indexOf(query, ignoreCase = true)

            while (startIndex >= 0) {
                addStyle(
                    style = SpanStyle(
                        color = foregroundColor,
                        background = backgroundColor,
                        fontWeight = FontWeight.Bold
                    ),
                    start = startIndex,
                    end = startIndex + query.length
                )
                startIndex = text.indexOf(query, startIndex + query.length, ignoreCase = true)
            }
        }
    } else {
        this
    }
}
