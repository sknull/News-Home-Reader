package de.visualdigits.newshomereader.domain.model.settings

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.ColorPickerFieldDescriptor
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.FieldsInitializer
import de.visualdigits.common.domain.model.configuration.IntFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.IntKeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_displayTheme
import de.visualdigits.compose.resources.label_hide_read
import de.visualdigits.compose.resources.label_keep_read_articles
import de.visualdigits.compose.resources.label_keep_unread_articles
import de.visualdigits.compose.resources.label_language
import de.visualdigits.compose.resources.label_load_articles
import de.visualdigits.compose.resources.label_refresh_interval
import de.visualdigits.compose.resources.label_refresh_wifi_only
import de.visualdigits.compose.resources.label_spotColor
import de.visualdigits.compose.resources.label_webDavDirectory
import de.visualdigits.compose.resources.label_webDavPassword
import de.visualdigits.compose.resources.label_webDavUrl
import de.visualdigits.compose.resources.label_webDavUser
import de.visualdigits.compose.resources.ok
import de.visualdigits.compose.resources.tooltip_displayTheme
import de.visualdigits.compose.resources.tooltip_hide_read
import de.visualdigits.compose.resources.tooltip_keep_read_articles
import de.visualdigits.compose.resources.tooltip_keep_unread_articles
import de.visualdigits.compose.resources.tooltip_language
import de.visualdigits.compose.resources.tooltip_load_articles
import de.visualdigits.compose.resources.tooltip_refresh_interval
import de.visualdigits.compose.resources.tooltip_refresh_wifi_only
import de.visualdigits.compose.resources.tooltip_spotColor
import de.visualdigits.compose.resources.tooltip_webDavDirectory
import de.visualdigits.compose.resources.tooltip_webDavPassword
import de.visualdigits.compose.resources.tooltip_webDavUrl
import de.visualdigits.compose.resources.tooltip_webDavUser
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum

typealias EncryptedString = String

@Immutable
class Settings(
    newFields: List<Field<*,*,SK>>? = null
): AbstractConfiguration<Settings, SK>(newFields?:setupFields()) {

    companion object : FieldsInitializer<SK> {
        override fun setupFields(): List<Field<*,*,SK>> {
            return listOf(

                /** The UI language. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = Language::class,
                        key = SK.language,
                        label = UiText.StringResourceId(Res.string.label_language),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_language),
                        options = { Language.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = Language
                    ),
                    valid = { value -> value != null }
                ),

                /** Display Theme. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = DisplayThemeEnum::class,
                        key = SK.displayTheme,
                        label =  UiText.StringResourceId(Res.string.label_displayTheme),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_displayTheme),
                        options = { DisplayThemeEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = DisplayThemeEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** The spot color. */
                Field(
                    descriptor = ColorPickerFieldDescriptor(
                        key = SK.spotColor,
                        label =  UiText.StringResourceId(Res.string.label_spotColor),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_spotColor),
                    ),
                    valid = { _ -> true }
                ),

                /** Refresh Interval. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = RefreshIntervalEnum::class,
                        key = SK.refreshInterval,
                        label =  UiText.StringResourceId(Res.string.label_refresh_interval),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_refresh_interval),
                        options = { RefreshIntervalEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = RefreshIntervalEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** Refresh only when connection is free of charge. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = BooleanEnum::class,
                        key = SK.refreshWifiOnly,
                        label =  UiText.StringResourceId(Res.string.label_refresh_wifi_only),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_refresh_wifi_only),
                        options = { BooleanEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = BooleanEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** Keep Read Articles. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = KeepArticlesEnum::class,
                        key = SK.keepReadArticles,
                        label =  UiText.StringResourceId(Res.string.label_keep_read_articles),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_keep_read_articles),
                        options = { KeepArticlesEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = KeepArticlesEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** Keep Unread Articles. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = KeepArticlesEnum::class,
                        key = SK.keepUnreadArticles,
                        label =  UiText.StringResourceId(Res.string.label_keep_unread_articles),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_keep_unread_articles),
                        options = { KeepArticlesEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = KeepArticlesEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** Load articles. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = BooleanEnum::class,
                        key = SK.loadArticles,
                        label =  UiText.StringResourceId(Res.string.label_load_articles),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_load_articles),
                        options = { BooleanEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = BooleanEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** Hide read items. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = BooleanEnum::class,
                        key = SK.hideRead,
                        label =  UiText.StringResourceId(Res.string.label_hide_read),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_hide_read),
                        options = { BooleanEnum.entries.map { e -> Triple(e.name, e.uiText, e.drawableResourceId) } },
                        keyFactory = BooleanEnum
                    ),
                    valid = { value -> value != null }
                ),

                /** The webDav host URL. */
                Field(
                    descriptor = StringFieldDescriptor(
                        key = SK.webDavUrl,
                        label = UiText.StringResourceId(Res.string.label_webDavUrl),
                        toolTip = UiText.StringResourceId(Res.string.tooltip_webDavUrl),
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                /** The webDav host Directory. */
                Field(
                    descriptor = StringFieldDescriptor(
                        key = SK.webDavDirectory,
                        label = UiText.StringResourceId(Res.string.label_webDavDirectory),
                        toolTip = UiText.StringResourceId(Res.string.tooltip_webDavDirectory),
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                /** The webDav user name. */
                Field(
                    descriptor = StringFieldDescriptor(
                        key = SK.webDavUser,
                        label = UiText.StringResourceId(Res.string.label_webDavUser),
                        toolTip = UiText.StringResourceId(Res.string.tooltip_webDavUser),
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                /** The webDav password. */
                Field(
                    descriptor = StringFieldDescriptor(
                        key = SK.webDavPassword,
                        label = UiText.StringResourceId(Res.string.label_webDavPassword),
                        toolTip = UiText.StringResourceId(Res.string.tooltip_webDavPassword),
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),


                /** Hidden field for maxImageSize. */
                Field(
                    descriptor = EnumFieldDescriptor(
                        fieldClass = Int::class,
                        visible = false,
                        key = SK.maxImageSize,
                        label =  UiText.StringResourceId(Res.string.ok),
                        keyFactory = IntKeyFactory
                    ),
                    valid = { value -> value != null }
                ),

                /** Hidden field for maxImageSize. */
                Field(
                    descriptor = IntFieldDescriptor(
                        visible = false,
                        key = SK.maxImageSize,
                        label =  UiText.StringResourceId(Res.string.ok),
                    ),
                    valid = { value -> value != null }
                ),

                /** Hidden field for feeds changed (dirty flag). */
                Field(
                    descriptor = EnumFieldDescriptor(
                        visible = false,
                        fieldClass = BooleanEnum::class,
                        key = SK.feedsChanged,
                        label =  UiText.StringResourceId(Res.string.ok),
                        keyFactory = BooleanEnum
                    ),
                    valid = { value -> value != null }
                ),
            )
        }
    }

    override fun createInstance(newFields: List<Field<*,*,SK>>): Settings {
        return Settings(newFields)
    }
}
