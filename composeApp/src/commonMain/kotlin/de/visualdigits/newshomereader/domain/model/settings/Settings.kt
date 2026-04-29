package de.visualdigits.newshomereader.domain.model.settings

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.IntFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.IntKeyFactory
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_displayTheme
import de.visualdigits.compose.resources.label_hide_read
import de.visualdigits.compose.resources.label_keep_read_articles
import de.visualdigits.compose.resources.label_keep_unread_articles
import de.visualdigits.compose.resources.label_language
import de.visualdigits.compose.resources.label_load_articles
import de.visualdigits.compose.resources.label_refresh_interval
import de.visualdigits.compose.resources.label_refresh_wifi_only
import de.visualdigits.compose.resources.ok
import de.visualdigits.compose.resources.tooltip_displayTheme
import de.visualdigits.compose.resources.tooltip_hide_read
import de.visualdigits.compose.resources.tooltip_keep_read_articles
import de.visualdigits.compose.resources.tooltip_keep_unread_articles
import de.visualdigits.compose.resources.tooltip_language
import de.visualdigits.compose.resources.tooltip_load_articles
import de.visualdigits.compose.resources.tooltip_refresh_interval
import de.visualdigits.compose.resources.tooltip_refresh_wifi_only
import de.visualdigits.newshomereader.domain.model.type.Language

class Settings(
    fields: LinkedHashMap<SK, Field<*,*,SK>> = LinkedHashMap()
): AbstractConfiguration<Settings, SK>(fields) {

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

    override fun createInstance(newFields: LinkedHashMap<SK, Field<*,*,SK>>): Settings {
        return Settings(newFields)
    }

}
