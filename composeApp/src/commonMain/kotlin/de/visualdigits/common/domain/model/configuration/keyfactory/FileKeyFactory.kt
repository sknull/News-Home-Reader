package de.visualdigits.common.domain.model.configuration.keyfactory

import java.io.File

class FileKeyFactory {

    companion object : KeyFactory<File> {

        override fun fromString(value: String?): File?  = value?.let { v -> File(v) }

        override fun fromValue(value: Any?): File? {
            return when (value) {
                is String -> fromString(value)
                is File -> value
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? = (value as? File)?.canonicalPath
    }
}
