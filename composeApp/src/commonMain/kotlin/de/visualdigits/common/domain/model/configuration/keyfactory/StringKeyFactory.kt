package de.visualdigits.common.domain.model.configuration.keyfactory

class StringKeyFactory {

    companion object : KeyFactory<String> {

        override fun fromString(value: String?): String?  = value

        override fun fromValue(value: Any?): String? {
            return when (value) {
                is String -> fromString(value)
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? = value as? String
    }
}
