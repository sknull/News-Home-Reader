package de.visualdigits.newshomereader.data.mapper

import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.KeepArticlesEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.RefreshIntervalEnum
import de.visualdigits.newshomereader.SettingsEntity
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.domain.model.settings.Settings
import de.visualdigits.newshomereader.domain.model.type.Language

fun Settings.toSettingsEntity(): SettingsEntity {
    val settingsEntity = SettingsEntity(
        id = 0,
        displayTheme = get<DisplayThemeEnum>(SK.displayTheme)?.name ?: "LIGHT",
        language = get<Language>(SK.language)?.name ?: "EN",
        hideRead = get<BooleanEnum>(SK.hideRead)?.booleanValue ?: false,
        loadArticles = get<BooleanEnum>(SK.loadArticles)?.booleanValue ?: false,
        refreshInterval = get<RefreshIntervalEnum>(SK.refreshInterval)?.name ?: "MINUTES_60",
        keepReadArticles = get<KeepArticlesEnum>(SK.keepReadArticles)?.name ?: "DAYS_30",
        keepUnreadArticles = get<KeepArticlesEnum>(SK.keepUnreadArticles)?.name ?: "DAYS_30",
    )
    return settingsEntity
}

fun SettingsEntity.toSettings(): Settings {
    val settings = Settings()

    settings.set(SK.displayTheme, displayTheme)
    settings.set(SK.language, language)
    settings.set(SK.hideRead, hideRead)
    settings.set(SK.loadArticles, loadArticles)
    settings.set(SK.refreshInterval, refreshInterval)
    settings.set(SK.keepReadArticles, keepReadArticles)
    settings.set(SK.keepUnreadArticles, keepUnreadArticles)

    return settings
}
