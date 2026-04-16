package de.visualdigits.translation.util


/**
 * Takes care about some special chars before serialize them back to xml.
 */
fun String.escapeStringResource(): String {
    if (this.isEmpty()) return ""

    return this
        // Special chars for KMP
//            .replace("'", "\\'")      // Apostroph -> \'
        .replace("\"", "\\\"")    // Anführungszeichen -> \"
        .replace("%", "%%")       // Prozent -> %% (wegen String.format)
        .replace("\n", "\\n")

        // Special cases for KMP at the string start
        .let { s ->
            if (s.startsWith("?") || s.startsWith("@")) {
                "\\" + s          // ? -> \? and @ -> \@
            } else s
        }

        // White space protection
        .let { s ->
            if (s.startsWith(" ") || s.endsWith(" ")) {
                "\"$s\""          // Trailing and leading spaces -> " string "
            } else s
        }
}

/**
 * Takes care about some special chars before deserializing from xml.
 */
fun String.unescapeStringResource(): String {
    return this
        // Remove Android-Backslashes
        .replace("\\'", "'")
        .replace("\\\"", "\"")
        .replace("\\?", "?")
        .replace("\\@", "@")
        .replace("\n", "\\n")

        // resolve double quotes
        .replace("%%", "%")

        // Remove white space protectionn
        .let { if (it.startsWith("\"") && it.endsWith("\"")) it.removeSurrounding("\"") else it }
}
