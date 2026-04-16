package de.visualdigits.common.domain.model

import de.visualdigits.common.domain.model.configuration.AbstractFieldDescriptor

class KeyValue(
    val descriptor: AbstractFieldDescriptor<*,*,*>,
    val value: String? = null,
    val previousValue: String? = null,
    val newValue: String? = null,
) {

    override fun toString(): String = "keyValueAction: $descriptor=$value"
}
