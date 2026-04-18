package de.visualdigits.newshomereader.domain.model.errorhandling

import co.touchlab.kermit.Logger

inline fun <reified T : Any> T.kermitLogger(): Logger =
    Logger.withTag(T::class.simpleName ?: "Unknown")
