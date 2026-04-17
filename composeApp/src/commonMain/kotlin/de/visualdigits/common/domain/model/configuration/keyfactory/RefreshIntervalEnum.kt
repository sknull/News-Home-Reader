package de.visualdigits.common.domain.model.configuration.keyfactory

import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_15_minutes
import de.visualdigits.compose.resources.label_30_minutes
import de.visualdigits.compose.resources.label_45_minutes
import de.visualdigits.compose.resources.label_60_minutes
import org.jetbrains.compose.resources.DrawableResource

enum class RefreshIntervalEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val longValue: Long
) : StringResourceEnumerable<RefreshIntervalEnum> {

    MINUTES_15(UiText.StringResourceId(Res.string.label_15_minutes),  null, 15),
    MINUTES_30(UiText.StringResourceId(Res.string.label_30_minutes),  null, 30),
    MINUTES_45(UiText.StringResourceId(Res.string.label_45_minutes),  null, 45),
    MINUTES_60(UiText.StringResourceId(Res.string.label_60_minutes),  null, 60),
    ;

    override fun toString(): String = name.lowercase()

    companion object : KeyFactory<RefreshIntervalEnum> {

        override fun fromString(value: String?): RefreshIntervalEnum? {
            return entries.find { e -> e.name == value }
        }

        override fun fromValue(value: Any?): RefreshIntervalEnum? {
            return when (value) {
                is String -> fromString(value)
                is RefreshIntervalEnum -> value
                is Long -> entries.find { e -> e.longValue == value }
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            return (value as? RefreshIntervalEnum)?.name?:value?.toString()
        }
    }
}
