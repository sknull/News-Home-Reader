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

        // Wenn die relativeUrl bereits absolut ist (z.B. mit http:// beginnt),
        // wirft Ktor keinen Fehler, sondern parst sie normal.
        // Wir prüfen, ob ein Protokoll/Scheme vorhanden ist:
        if (relUrl.protocol.name.isNotEmpty()) {
            relativeUrl
        } else {
            // Kombiniert Basis-URL mit dem relativen Pfad/Query/Fragment
            URLBuilder().apply {
                takeFrom(absoluteUrl)
                takeFrom(relativeUrl)
            }.buildString()
        }
    } catch (_: Exception) {
        // Falls relativeUrl kein valides URL-Format hat (z.B. nur ein reiner Pfad ohne Scheme),
        // fängt Ktor das im oberen Konstruktor ab. Dann bauen wir sie hier zusammen:
        URLBuilder().apply {
            takeFrom(absoluteUrl)
            takeFrom(relativeUrl)
        }.buildString()
    }
}
