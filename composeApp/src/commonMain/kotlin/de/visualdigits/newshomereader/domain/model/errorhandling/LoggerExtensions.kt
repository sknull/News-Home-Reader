package de.visualdigits.newshomereader.domain.model.errorhandling

import co.touchlab.kermit.Logger
import kotlin.reflect.KClass

fun kermitLogger(tag: KClass<*>): Logger =
    Logger.withTag(tag.simpleName?.let { t -> "NHR_$t" } ?: "Unknown")

fun kermitLogger(tag: String): Logger =
    Logger.withTag("NHR_$tag")
