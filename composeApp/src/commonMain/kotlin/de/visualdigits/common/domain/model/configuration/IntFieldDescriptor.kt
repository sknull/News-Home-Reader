package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.configuration.keyfactory.IntKeyFactory
import org.jetbrains.compose.resources.StringResource

/**
 * Represents a field which is rendered as a text field in the UI.
 */
class IntFieldDescriptor<K : FieldKey<K>>(
    key: K,

    label: StringResource,
    toolTip: StringResource? = null,

    visible: Boolean = true,
    readOnly: Boolean = false,
): AbstractFieldDescriptor<Int, String, K>(
    fieldClass = Int::class,
    key = key,
    label = label,
    toolTip = toolTip,
    visible = visible,
    readOnly = readOnly,
    options = { listOf() },
    keyFactory = IntKeyFactory,
)

