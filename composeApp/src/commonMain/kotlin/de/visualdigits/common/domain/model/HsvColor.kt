package de.visualdigits.common.domain.model

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

data class HsvColor(
    val hue: Int,
    val saturation: Float,
    val value: Float
) {

    /**
     * Returns a Color representing the given [hue], [saturation] and [value] values.
     *
     * hue 0 - 360
     * saturation 0.0f - 1.0f
     * value 0.0f - 1.0f
     */
    fun toColor(hue: Int? = null, saturation: Float? = null, value: Float? = null, alpha: Float = 1.0f): Color {
        val hh = hue?:this.hue
        val ss = saturation?:this.saturation
        val vv = value?:this.value

        val c = vv * ss
        val x = c * (1 - abs((hh / 60f) % 2 - 1))
        val m = vv - c
        val (r, g, b) = when {
            hh < 60 -> Triple(c, x, 0f)
            hh < 120 -> Triple(x, c, 0f)
            hh < 180 -> Triple(0f, c, x)
            hh < 240 -> Triple(0f, x, c)
            hh < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(red = r + m, green = g + m, blue = b + m, alpha = alpha)
    }
}
