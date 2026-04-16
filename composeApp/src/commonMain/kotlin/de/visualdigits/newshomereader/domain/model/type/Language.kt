package de.visualdigits.newshomereader.domain.model.type

import de.visualdigits.common.domain.model.StringResourceEnumerable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.flag_de
import de.visualdigits.compose.resources.flag_en
import de.visualdigits.compose.resources.language_de
import de.visualdigits.compose.resources.language_en
import org.jetbrains.compose.resources.DrawableResource
import java.util.Locale

enum class Language(
    override val uiText: UiText,
    override val drawableResourceId: DrawableResource?,
    val locale: Locale
) : StringResourceEnumerable<Language> {

    DE(UiText.StringResourceId(Res.string.language_de), Res.drawable.flag_de, Locale.GERMANY),
    EN(UiText.StringResourceId(Res.string.language_en), Res.drawable.flag_en, Locale.US),
    ;

    companion object : KeyFactory<Language> {

        override fun fromString(value: String?): Language? {
            return entries.find { e -> e.name == value }
        }

        override fun fromValue(value: Any?): Language? {
            return when (value) {
                is String -> fromString(value)
                is Language -> value
                is Locale -> Language.entries.find { e -> e.locale == value }
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            return (value as? Language)?.name?:value?.toString()
        }
    }
}
