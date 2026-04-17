package de.visualdigits.common.domain.model.configuration.keyfactory

class IntKeyFactory {

    companion object : KeyFactory<Int> {

        override fun fromString(value: String?): Int?  = value?.toInt()

        override fun fromValue(value: Any?): Int? {
            return when (value) {
                is String -> fromString(value)
                is Int -> value
                is Number -> value.toInt()
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? = value as? String
    }
}
