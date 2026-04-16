package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import org.jetbrains.compose.resources.DrawableResource

class Field<V : Any, S : Any, K : FieldKey<K>>(
    val descriptor: AbstractFieldDescriptor<V, S, K>,
    var value: V? = null,
    var enabled: Boolean = true,
    val valid: (value: Any?) -> Boolean = { _ -> true }
) {

    override fun toString(): String {
        return "${descriptor.label} [${descriptor.fieldClass.simpleName}]: $value"
    }

    fun fromString(s: String?): V? {
        return descriptor.keyFactory.fromString(s)
    }

    fun stringValue(): String? {
        return descriptor.keyFactory.stringValue(value)
    }

    fun currentOption(): Triple<String, UiText?, DrawableResource?> {
        val sv = stringValue()?.uppercase()
        val options = descriptor.options()
        return options
            .find { o -> o.first.uppercase() == sv }
            ?:options.first()
    }

    @Suppress("UNCHECKED_CAST")
    fun copyUnsafe(value: Any? = null): Field<V, S, K> {
        return Field(descriptor, (value as? V)?:this.value, enabled, valid)
    }

    fun copy(value: V?): Field<V, S, K> {
        return Field(descriptor, value?:this.value, enabled, valid)
    }

    @Suppress("UNCHECKED_CAST")
    fun setUnsafe(value: Any?) {
        val fromValue = descriptor.keyFactory.fromValue(value)
        this.value = fromValue
    }
}
