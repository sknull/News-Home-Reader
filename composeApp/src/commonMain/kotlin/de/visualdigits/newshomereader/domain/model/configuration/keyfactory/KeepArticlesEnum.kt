package de.visualdigits.newshomereader.domain.model.configuration.keyfactory

import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_14_days
import de.visualdigits.compose.resources.label_1_day
import de.visualdigits.compose.resources.label_30_days
import de.visualdigits.compose.resources.label_3_days
import de.visualdigits.compose.resources.label_60_days
import de.visualdigits.compose.resources.label_7_days
import org.jetbrains.compose.resources.DrawableResource

enum class KeepArticlesEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val longValue: Long
) : StringResourceEnumerable<KeepArticlesEnum> {

   DAYS_1(UiText.StringResourceId(Res.string.label_1_day),  null, 1),
   DAYS_3(UiText.StringResourceId(Res.string.label_3_days),  null, 3),
   DAYS_7(UiText.StringResourceId(Res.string.label_7_days),  null, 7),
   DAYS_14(UiText.StringResourceId(Res.string.label_14_days),  null, 14),
   DAYS_30(UiText.StringResourceId(Res.string.label_30_days),  null, 30),
   DAYS_60(UiText.StringResourceId(Res.string.label_60_days),  null, 60),
    ;

    override fun toString(): String = name

    companion object : KeyFactory<KeepArticlesEnum> {

        override val options: List<Triple<KeepArticlesEnum, UiText?, DrawableResource?>> = entries.map { e -> Triple(e, e.uiText, e.drawableResourceId) }

        override fun fromString(value: String?): KeepArticlesEnum? {
            return entries.find { e -> e.name.equals(value, ignoreCase = true) }
        }

        override fun fromValue(value: Any?): KeepArticlesEnum? {
            return when (value) {
                is String -> fromString(value)
                is KeepArticlesEnum -> value
                is Long -> entries.find { e -> e.longValue == value }
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            return (value as? KeepArticlesEnum)?.name?:value?.toString()
        }
    }
}
