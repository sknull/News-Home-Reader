package de.visualdigits.common.domain.model.configuration.keyfactory

class StringListKeyFactory {

    companion object : KeyFactory<MutableList<String>> {

        override fun fromString(value: String?): MutableList<String> {
            return if (value?.isNotEmpty() == true) {
                value
                    .split(",")
                    .map { v -> v.trim() }
                    .toMutableList()
            } else {
                mutableListOf()
            }
        }

        override fun fromValue(value: Any?): MutableList<String>? {
            return when (value) {
                is String -> fromString(value)
                is List<*> -> value.map { e -> e.toString() }.toMutableList()
                else -> null
            }
        }

        override fun stringValue(value: Any?): String? {
            val s = (value as? List<String>)?.joinToString(",")
            return s
        }
    }
}
