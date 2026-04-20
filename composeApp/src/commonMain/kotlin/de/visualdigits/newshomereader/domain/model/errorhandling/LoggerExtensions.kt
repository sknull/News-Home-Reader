package de.visualdigits.newshomereader.domain.model.errorhandling

import co.touchlab.kermit.Logger
import org.koin.ext.getFullName
import kotlin.reflect.KClass

fun kermitLogger(tag: KClass<*>): Logger =
    Logger.withTag(tag.getFullName())

fun kermitLogger(tag: String): Logger =
    Logger.withTag(tag)
