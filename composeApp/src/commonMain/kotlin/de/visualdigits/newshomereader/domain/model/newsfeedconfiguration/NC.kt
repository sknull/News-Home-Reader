package de.visualdigits.newshomereader.domain.model.newsfeedconfiguration

import de.visualdigits.common.domain.model.configuration.FieldKey

enum class NC : FieldKey<NC> {

    feedName,
    mainGroupName,
    subGroupName,
    imageUrl,
    url,
    stopWords,
    ;
}
