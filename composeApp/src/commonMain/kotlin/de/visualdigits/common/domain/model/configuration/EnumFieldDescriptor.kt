package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

/**
 * Represents a field which is rendered as a combobox in the UI.
 */
class EnumFieldDescriptor<V : Any, K : FieldKey<K>>(
    fieldClass: KClass<V>,

    key: K,

    label: StringResource,
    toolTip: StringResource? = null,

    visible: Boolean = true,
    readOnly: Boolean = false,

    options: () -> List<Triple<String, UiText?, DrawableResource?>> = { listOf() },

    keyFactory: KeyFactory<V>
): AbstractFieldDescriptor<V, V, K>(
    fieldClass = fieldClass,
    key = key,
    label = label,
    toolTip = toolTip,
    visible = visible,
    readOnly = readOnly,
    options = options,
    keyFactory = keyFactory
)

