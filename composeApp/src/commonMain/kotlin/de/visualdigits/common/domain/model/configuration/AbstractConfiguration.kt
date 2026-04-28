package de.visualdigits.common.domain.model.configuration

/**
 * Base class for all configuration classes.
 */
abstract class AbstractConfiguration<T : AbstractConfiguration<T, K>, K : FieldKey<K>>(
    val fields: LinkedHashMap<K, Field<*,*,K>> = LinkedHashMap()
) {

    init {
        if (fields.isEmpty()) {
            val setupFields = setupFields()
            setupFields.forEach { f ->
                fields[f.descriptor.key] = f
                f.configuration = this
            }
        }
    }

    override fun toString(): String {
        return fields.toList().joinToString(", ") { e -> "${e.first}=\"${e.second}\"" }
    }

    abstract fun setupFields(): List<Field<*,*,K>>

    protected abstract fun createInstance(newFields: LinkedHashMap<K, Field<*,*,K>>): T

    inline fun <reified V : Any> get(key: K): V? {
        val field = fields[key]
        return field?.value as? V
    }

    @Suppress("UNCHECKED_CAST")
    fun set(key: K, value: Any?) {
        val field = fields[key]
        field?.setUnsafe(value)
    }

    /**
     * Determines if the actual value of the field is valid or not.
     * Returns a pair of <[true | false], description-resource-id>
     * When the [key] is not given it checks whether the entire configuration is valid or not.
     */
    open fun valid(key: K): Boolean? {
        return fields.values.all { field -> field.valid(field.value) }
    }

    fun copy(key: K? = null, value: String? = null): T {
        val newFields = LinkedHashMap<K, Field<*,*,K>>()
        fields.values.forEach { f ->
            if (f.descriptor.key == key) {
                newFields[f.descriptor.key] = f.copyUnsafe(f.fromString(value))
            } else {
                newFields[f.descriptor.key] = f.copy()
            }
        }

        val createInstance = createInstance(newFields)
        createInstance.fields.values.forEach { field -> field.configuration = createInstance }
        return createInstance
    }
}
