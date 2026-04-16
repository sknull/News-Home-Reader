package de.visualdigits.common.domain.model.configuration

interface FieldKey<K : FieldKey<K>> {

    val name: String

    companion object {

        inline fun <reified K> fromString(name: String): K?
                where K : Enum<K>, K : FieldKey<K> {
            return enumValues<K>().find { it.name == name }
        }
    }
}

class SpacerKey(override val name: String = "SPACER") : FieldKey<SpacerKey>
