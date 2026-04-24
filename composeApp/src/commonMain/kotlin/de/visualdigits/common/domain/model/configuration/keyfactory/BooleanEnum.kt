package de.visualdigits.common.domain.model.configuration.keyfactory

import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.`false`
import de.visualdigits.compose.resources.`true`
import org.jetbrains.compose.resources.DrawableResource

enum class BooleanEnum(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val booleanValue: Boolean
) : StringResourceEnumerable<BooleanEnum> {

    TRUE(UiText.StringResourceId(Res.string.`true`),  null, true),
    FALSE(UiText.StringResourceId(Res.string.`false`),  null, false),
    ;

    override fun toString(): String = name.lowercase()

    companion object : KeyFactory<BooleanEnum> {

        override fun fromString(value: String?): BooleanEnum? {
            return entries.find { e -> e.name == value?.uppercase() }
        }

        override fun fromValue(value: Any?): BooleanEnum? {
            val v = when (value) {
                is String -> fromString(value)
                is Boolean -> entries.find { e -> e.booleanValue == value }
                is BooleanEnum -> value
                else -> null
            }
            return v
        }

        override fun stringValue(value: Any?): String? = (value as? BooleanEnum)?.name?:value?.toString()?.lowercase()
    }
}
