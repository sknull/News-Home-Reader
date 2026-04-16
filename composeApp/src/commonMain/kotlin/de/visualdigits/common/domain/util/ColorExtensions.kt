package de.visualdigits.common.domain.util

import androidx.compose.ui.graphics.Color
import de.visualdigits.common.domain.model.HsvColor

/**
 * Returns a copy of this color with the given [hue], [saturation] and [value] values.
 *
 * hue 0 - 360
 * saturation 0.0f - 1.0f
 * value 0.0f - 1.0f
 *
 */
fun Color.copy(hue: Int? = null, saturation: Float? = null, value: Float? = null, alpha: Float = 1.0f): Color {
    val hsvColor = toHsvColor()
    return hsvColor.toColor(hue = hue, saturation = saturation, value = value, alpha = alpha)
}

fun Color.copyFactor(hueShift: Int = 0, saturationFactor: Float = 1.0f, valueFactor: Float = 1.0f, alphaFactor: Float = 1.0f): Color {
    val hsvColor = toHsvColor()

    return hsvColor.toColor(hue = (hsvColor.hue + hueShift) % 360, saturation = hsvColor.saturation * saturationFactor, value = hsvColor.value * valueFactor, alpha = alpha * alphaFactor)
}

/**
 * Returns this color expressed as hue, saturation, value
 *
 * hue 0 - 360
 * saturation 0.0f - 1.0f
 * value 0.0f - 1.0f
 */
fun Color.toHsvColor(): HsvColor {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, maxOf(g, b))
    val min = minOf(r, minOf(g, b))
    val delta = max - min

    var h = 0f
    if (delta != 0f) {
        h = when (max) {
            r -> 60f * (((g - b) / delta) % 6f)
            g -> 60f * (((b - r) / delta) + 2f)
            else -> 60f * (((r - g) / delta) + 4f)
        }
    }
    if (h < 0f) h += 360f

    val s = if (max == 0f) 0f else delta / max
    max

    return HsvColor(hue = h.toInt(), saturation = s, value = max)
}
