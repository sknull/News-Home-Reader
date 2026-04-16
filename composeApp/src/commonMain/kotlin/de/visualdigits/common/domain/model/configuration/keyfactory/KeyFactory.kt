package de.visualdigits.common.domain.model.configuration.keyfactory

interface KeyFactory<T> {

    fun fromString(value: String?): T?

    fun fromValue(value: Any?): T?

    fun stringValue(value: Any?): String?
}
