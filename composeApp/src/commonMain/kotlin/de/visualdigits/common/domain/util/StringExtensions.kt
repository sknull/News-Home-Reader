package de.visualdigits.common.domain.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Expands the variables in format ${name}
 */
fun String.expandVariables(values: Map<String, String?>): String {
    return replace("\\\$\\{(.*?)\\}".toRegex()) { match ->
        val key = match.groupValues[1]
        val value = values[key]
        if (value?.isNotEmpty() == true) value else ""
    }
}

fun String.toComposeColor(): Color {
    val hex = this.removePrefix("#")

    return when (hex.length) {
        6 -> {
            Color("FF$hex".toLong(16))
        }
        8 -> {
            Color(hex.toLong(16))
        }
        else -> throw IllegalArgumentException("Invalid format: $this")
    }
}

fun Color.toWebColor(): String {
    return String.format("#%08X", toArgb())
}

fun Color.toWebColorShort(): String {
    return String.format("#%06X", 0xFFFFFF and toArgb())
}
