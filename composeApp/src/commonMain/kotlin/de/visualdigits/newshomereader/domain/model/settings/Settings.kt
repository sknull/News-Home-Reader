package de.visualdigits.newshomereader.domain.model.settings

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.ColorPickerFieldDescriptor
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.IntFieldDescriptor
import de.visualdigits.common.domain.model.configuration.PasswordFieldDescriptor
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

@Immutable
class Settings(
    values: Map<SK, Any?> = mapOf(),
): AbstractConfiguration<Settings, SK>(values, DESCRIPTORS) {

    companion object {
        val DESCRIPTORS = listOf(

            /** The UI language. */
            EnumFieldDescriptor(
                fieldClass = Language::class,
                key = SK.language,
                label = UiText.StringResourceId(Res.string.label_language),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_language),
                options = { Language.options },
                keyFactory = Language,
                default = Language.EN
            ),

            /** Display Theme. */
            EnumFieldDescriptor(
                fieldClass = DisplayThemeEnum::class,
                key = SK.displayTheme,
                label =  UiText.StringResourceId(Res.string.label_displayTheme),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_displayTheme),
                options = { DisplayThemeEnum.options },
                keyFactory = DisplayThemeEnum,
                default = DisplayThemeEnum.LIGHT
            ),

            /** The spot color. */
            ColorPickerFieldDescriptor(
                key = SK.spotColor,
                label =  UiText.StringResourceId(Res.string.label_spotColor),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_spotColor),
                default = DisplayThemeEnum.SPOT_COLOR_DEFAULT
            ),

            /** Refresh Interval. */
            EnumFieldDescriptor(
                fieldClass = RefreshIntervalEnum::class,
                key = SK.refreshInterval,
                label =  UiText.StringResourceId(Res.string.label_refresh_interval),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_refresh_interval),
                options = { RefreshIntervalEnum.options },
                keyFactory = RefreshIntervalEnum,
                default = RefreshIntervalEnum.MINUTES_60
            ),

            /** Refresh only when connection is free of charge. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                key = SK.refreshWifiOnly,
                label =  UiText.StringResourceId(Res.string.label_refresh_wifi_only),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_refresh_wifi_only),
                options = { BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.TRUE
            ),

            /** Keep Read Articles. */
            EnumFieldDescriptor(
                fieldClass = KeepArticlesEnum::class,
                key = SK.keepReadArticles,
                label =  UiText.StringResourceId(Res.string.label_keep_read_articles),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_keep_read_articles),
                options = { KeepArticlesEnum.options },
                keyFactory = KeepArticlesEnum,
                default = KeepArticlesEnum.DAYS_3
            ),

            /** Keep Unread Articles. */
            EnumFieldDescriptor(
                fieldClass = KeepArticlesEnum::class,
                key = SK.keepUnreadArticles,
                label =  UiText.StringResourceId(Res.string.label_keep_unread_articles),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_keep_unread_articles),
                options = { KeepArticlesEnum.options },
                keyFactory = KeepArticlesEnum,
                default = KeepArticlesEnum.DAYS_3
            ),

            /** Load articles. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                key = SK.loadArticles,
                label =  UiText.StringResourceId(Res.string.label_load_articles),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_load_articles),
                options = { BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.TRUE
            ),

            /** Hide read items. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                key = SK.hideRead,
                label =  UiText.StringResourceId(Res.string.label_hide_read),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_hide_read),
                options = { BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.TRUE
            ),

            /** The webDav host URL. */
            StringFieldDescriptor(
                key = SK.webDavUrl,
                label = UiText.StringResourceId(Res.string.label_webDavUrl),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavUrl),
            ),

            /** The webDav host Directory. */
            StringFieldDescriptor(
                key = SK.webDavDirectory,
                label = UiText.StringResourceId(Res.string.label_webDavDirectory),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavDirectory),
            ),

            /** The webDav user name. */
            StringFieldDescriptor(
                key = SK.webDavUser,
                label = UiText.StringResourceId(Res.string.label_webDavUser),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavUser),
            ),

            /** The webDav password. */
            PasswordFieldDescriptor(
                key = SK.webDavPassword,
                label = UiText.StringResourceId(Res.string.label_webDavPassword),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavPassword),
            ),


            /** Hidden field for maxImageSize. */
            EnumFieldDescriptor(
                fieldClass = Int::class,
                visible = false,
                key = SK.maxImageSize,
                label =  UiText.StringResourceId(Res.string.ok),
                keyFactory = IntKeyFactory
            ),

            /** Hidden field for maxImageSize. */
            IntFieldDescriptor(
                visible = false,
                key = SK.maxImageSize,
                label =  UiText.StringResourceId(Res.string.ok),
            ),

            /** Hidden field for feeds changed (dirty flag). */
            EnumFieldDescriptor(
                visible = false,
                fieldClass = BooleanEnum::class,
                key = SK.feedsChanged,
                label =  UiText.StringResourceId(Res.string.ok),
                keyFactory = BooleanEnum
            ),
        )
    }

    override fun createInstance(newValues: Map<SK, Any?>): Settings {
        return Settings(newValues)
    }
}
