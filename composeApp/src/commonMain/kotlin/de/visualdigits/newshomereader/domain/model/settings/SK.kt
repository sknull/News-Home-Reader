package de.visualdigits.newshomereader.domain.model.settings

import de.visualdigits.common.domain.model.configuration.FieldKey

enum class SK : FieldKey<SK> {

    displayTheme,
    spotColor,
    language,
    refreshInterval,
    refreshWifiOnly,
    loadArticles,
    hideRead,
    keepReadArticles,
    keepUnreadArticles,
    webDavUrl,
    webDavDirectory,
    webDavUser,
    webDavPassword,

    maxImageSize,
    feedsChanged
    ;
}
