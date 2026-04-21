package de.visualdigits.newshomereader.presentation.util

import java.net.URI


fun makeUrlAbsolute(
    absoluteUrl: String,
    relativeUrl: String
): String {
    val rel = URI(relativeUrl)
    return if (!rel.isAbsolute) {
        val abs = URI(absoluteUrl)
        val absoluteUrl = URI(abs.scheme, abs.userInfo, abs.host, abs.port, rel.path, rel.query, rel.fragment)
        absoluteUrl.toString()
    } else relativeUrl
}
