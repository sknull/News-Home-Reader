package de.visualdigits.newshomereader.domain.model.type

import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import de.visualdigits.common.domain.model.ui.StringResourceEnumerable
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.flag_de
import de.visualdigits.compose.resources.flag_en
import de.visualdigits.compose.resources.language_de
import de.visualdigits.compose.resources.language_en
import org.jetbrains.compose.resources.DrawableResource

enum class Language(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val localeCode: String
) : StringResourceEnumerable<Language> {

    DE(UiText.StringResourceId(Res.string.language_de), Res.drawable.flag_de, "de"),
    EN(UiText.StringResourceId(Res.string.language_en), Res.drawable.flag_en, "en"),
    ;

    companion object : KeyFactory<Language> {

        override val options: List<Triple<Language, UiText?, DrawableResource?>> = entries.map { e -> Triple(e, e.uiText, e.drawableResourceId) }

        override fun fromString(value: String?): Language? {
            return entries.find { e -> e.localeCode == value }
        }

        override fun fromValue(value: Any?): Language? {
            return when (value) {
                is String -> fromString(value)
                is Language -> value
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            return (value as? Language)?.name?:value?.toString()
        }
    }
}
