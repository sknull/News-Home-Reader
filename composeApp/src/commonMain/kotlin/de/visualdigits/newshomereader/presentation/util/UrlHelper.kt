package de.visualdigits.newshomereader.presentation.util

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.takeFrom

fun makeUrlAbsolute(
    absoluteUrl: String,
    relativeUrl: String
): String {
    return try {
        val relUrl = Url(relativeUrl)
        if (relUrl.protocol.name.isNotEmpty() && !relativeUrl.startsWith("/")) {
            relativeUrl
        } else {
            URLBuilder().apply {
                takeFrom(absoluteUrl)
                takeFrom(relativeUrl)
            }.buildString()
        }
    } catch (_: Exception) {
        URLBuilder().apply {
            takeFrom(absoluteUrl)
            takeFrom(relativeUrl)
        }.buildString()
    }
}
