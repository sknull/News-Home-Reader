package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

/**
 * Represents a field which is rendered as a combobox in the UI.
 * Similar to EnumFieldDescriptor but has not an enum as value.
 * Instead it can provide options from elsewhere.
 */
class ReferenceListFieldDescriptor<V : Any, K : FieldKey<K>>(
    fieldClass: KClass<V>,

    key: K,

    label: StringResource,
    toolTip: StringResource? = null,

    visible: Boolean = true,
    readOnly: Boolean = false,

    options: (AbstractConfiguration<*, *>) -> List<Triple<String, UiText?, DrawableResource?>> = { listOf() },

    keyFactory: KeyFactory<V>
): AbstractFieldDescriptor<V, V, K>(
    fieldClass = fieldClass,
    key = key,
    label = label,
    toolTip = toolTip,
    visible = visible,
    readOnly = readOnly,
    options = options as (AbstractConfiguration<*, *>) -> List<Triple<String, UiText?, DrawableResource?>>,
    keyFactory = keyFactory
) {
    override fun copy(): ReferenceListFieldDescriptor<V, K> {
        return ReferenceListFieldDescriptor(
            fieldClass = fieldClass,
            key = key,
            label = label,
            toolTip = toolTip,
            visible = visible,
            readOnly = readOnly,
            options = options,
            keyFactory = keyFactory
        )
    }
}
