package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.IntKeyFactory

/**
 * Represents a field which is rendered as a text field in the UI.
 */
class IntFieldDescriptor<K : FieldKey<K>>(
    key: K,

    label: UiText,
    toolTip: UiText? = null,

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
