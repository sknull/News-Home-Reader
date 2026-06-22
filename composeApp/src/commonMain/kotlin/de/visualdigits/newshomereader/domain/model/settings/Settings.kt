package de.visualdigits.newshomereader.domain.model.settings

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.ColorPickerFieldDescriptor
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.IntFieldDescriptor
import de.visualdigits.common.domain.model.configuration.PasswordFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.IntKeyFactory
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.presentation.components.StudioClockColors
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.group_display_theme
import de.visualdigits.compose.resources.group_feed_seetings
import de.visualdigits.compose.resources.group_webdav
import de.visualdigits.compose.resources.label_backgroundColor
import de.visualdigits.compose.resources.label_buttonColor
import de.visualdigits.compose.resources.label_clockColor
import de.visualdigits.compose.resources.label_hide_read
import de.visualdigits.compose.resources.label_keep_read_articles
import de.visualdigits.compose.resources.label_keep_unread_articles
import de.visualdigits.compose.resources.label_language
import de.visualdigits.compose.resources.label_load_articles
import de.visualdigits.compose.resources.label_prefetch_images
import de.visualdigits.compose.resources.label_refresh_interval
import de.visualdigits.compose.resources.label_refresh_wifi_only
import de.visualdigits.compose.resources.label_spotColor
import de.visualdigits.compose.resources.label_textColor
import de.visualdigits.compose.resources.label_webDavDirectory
import de.visualdigits.compose.resources.label_webDavPassword
import de.visualdigits.compose.resources.label_webDavUrl
import de.visualdigits.compose.resources.label_webDavUser
import de.visualdigits.compose.resources.tooltip_backgroundColor
import de.visualdigits.compose.resources.tooltip_buttonColor
import de.visualdigits.compose.resources.tooltip_clockColor
import de.visualdigits.compose.resources.tooltip_hide_read
import de.visualdigits.compose.resources.tooltip_keep_read_articles
import de.visualdigits.compose.resources.tooltip_keep_unread_articles
import de.visualdigits.compose.resources.tooltip_language
import de.visualdigits.compose.resources.tooltip_load_articles
import de.visualdigits.compose.resources.tooltip_prefetch_images
import de.visualdigits.compose.resources.tooltip_refresh_interval
import de.visualdigits.compose.resources.tooltip_refresh_wifi_only
import de.visualdigits.compose.resources.tooltip_spotColor
import de.visualdigits.compose.resources.tooltip_textColor
import de.visualdigits.compose.resources.tooltip_webDavDirectory
import de.visualdigits.compose.resources.tooltip_webDavPassword
import de.visualdigits.compose.resources.tooltip_webDavUrl
import de.visualdigits.compose.resources.tooltip_webDavUser
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.newshomereader.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.domain.model.type.Language
import de.visualdigits.newshomereader.presentation.style.BACKGROUND_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.SPOT_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.TEXT_COLOR_DEFAULT

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
                options = { _, _ -> Language.options },
                keyFactory = Language,
                default = Language.EN
            ),

            /** The background color. */
            ColorPickerFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_display_theme),
                key = SK.backgroundColor,
                label = UiText.StringResourceId(Res.string.label_backgroundColor),
                toolTip = UiText.StringResourceId(Res.string.tooltip_backgroundColor),
                default = BACKGROUND_COLOR_DEFAULT,
            ),

            /** The button color. */
            ColorPickerFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_display_theme),
                key = SK.buttonColor,
                label = UiText.StringResourceId(Res.string.label_buttonColor),
                toolTip = UiText.StringResourceId(Res.string.tooltip_buttonColor),
                default = BUTTON_COLOR_DEFAULT,
            ),

            /** The text color. */
            ColorPickerFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_display_theme),
                key = SK.textColor,
                label = UiText.StringResourceId(Res.string.label_textColor),
                toolTip = UiText.StringResourceId(Res.string.tooltip_textColor),
                default = TEXT_COLOR_DEFAULT,
            ),

            /** The spot color. */
            ColorPickerFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_display_theme),
                key = SK.spotColor,
                label = UiText.StringResourceId(Res.string.label_spotColor),
                toolTip = UiText.StringResourceId(Res.string.tooltip_spotColor),
                default = SPOT_COLOR_DEFAULT,
            ),

            /** The studio clock color. */
            ColorPickerFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_display_theme),
                key = SK.clockColor,
                label =  UiText.StringResourceId(Res.string.label_clockColor),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_clockColor),
                default = StudioClockColors.STUDIO_CLOCK_COLOR_DEFAULT
            ),

            /** Refresh Interval. */
            EnumFieldDescriptor(
                fieldClass = RefreshIntervalEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.refreshInterval,
                label =  UiText.StringResourceId(Res.string.label_refresh_interval),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_refresh_interval),
                options = { _, _ -> RefreshIntervalEnum.options },
                keyFactory = RefreshIntervalEnum,
                default = RefreshIntervalEnum.MINUTES_60
            ),

            /** Refresh only when connection is free of charge. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.refreshWifiOnly,
                label =  UiText.StringResourceId(Res.string.label_refresh_wifi_only),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_refresh_wifi_only),
                options = { _, _ -> BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.TRUE
            ),

            /** Keep Read Articles. */
            EnumFieldDescriptor(
                fieldClass = KeepArticlesEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.keepReadArticles,
                label =  UiText.StringResourceId(Res.string.label_keep_read_articles),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_keep_read_articles),
                options = { _, _ -> KeepArticlesEnum.options },
                keyFactory = KeepArticlesEnum,
                default = KeepArticlesEnum.DAYS_3
            ),

            /** Keep Unread Articles. */
            EnumFieldDescriptor(
                fieldClass = KeepArticlesEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.keepUnreadArticles,
                label =  UiText.StringResourceId(Res.string.label_keep_unread_articles),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_keep_unread_articles),
                options = { _, _ -> KeepArticlesEnum.options },
                keyFactory = KeepArticlesEnum,
                default = KeepArticlesEnum.DAYS_3
            ),

            /** Load articles. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.loadArticles,
                label =  UiText.StringResourceId(Res.string.label_load_articles),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_load_articles),
                options = { _, _ -> BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.TRUE
            ),

            /** Prefetch images. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.prefetchImages,
                label =  UiText.StringResourceId(Res.string.label_prefetch_images),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_prefetch_images),
                options = { _, _ -> BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.FALSE
            ),

            /** Hide read items. */
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                group = UiText.StringResourceId(Res.string.group_feed_seetings),
                key = SK.hideRead,
                label =  UiText.StringResourceId(Res.string.label_hide_read),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_hide_read),
                options = { _, _ -> BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.TRUE
            ),

            /** The webDav host URL. */
            StringFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_webdav),
                key = SK.webDavUrl,
                label = UiText.StringResourceId(Res.string.label_webDavUrl),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavUrl),
            ),

            /** The webDav host Directory. */
            StringFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_webdav),
                key = SK.webDavDirectory,
                label = UiText.StringResourceId(Res.string.label_webDavDirectory),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavDirectory),
            ),

            /** The webDav user name. */
            StringFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_webdav),
                key = SK.webDavUser,
                label = UiText.StringResourceId(Res.string.label_webDavUser),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavUser),
            ),

            /** The webDav password. */
            PasswordFieldDescriptor(
                group = UiText.StringResourceId(Res.string.group_webdav),
                key = SK.webDavPassword,
                label = UiText.StringResourceId(Res.string.label_webDavPassword),
                toolTip = UiText.StringResourceId(Res.string.tooltip_webDavPassword),
            ),


            /** Hidden field for maxImageSize. */
            EnumFieldDescriptor(
                fieldClass = Int::class,
                visible = false,
                key = SK.maxImageSize,
                label =  UiText.DynamicString(""),
                keyFactory = IntKeyFactory
            ),

            /** Hidden field for maxImageSize. */
            IntFieldDescriptor(
                visible = false,
                key = SK.maxImageSize,
                label =  UiText.DynamicString(""),
            ),

            /** Hidden field for feeds changed (dirty flag). */
            EnumFieldDescriptor(
                visible = false,
                fieldClass = BooleanEnum::class,
                key = SK.feedsChanged,
                label =  UiText.DynamicString(""),
                keyFactory = BooleanEnum
            ),
        )
    }

    override fun createInstance(newValues: Map<SK, Any?>): Settings {
        return Settings(newValues)
    }
}
