package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.StringKeyFactory

/**
 * Represents a field which is rendered as a text field in the UI.
 */
class StringFieldDescriptor<K : FieldKey<K>>(
    key: K,

    label: UiText,
    toolTip: UiText? = null,

    visible: Boolean = true,
    readOnly: Boolean = false,
): AbstractFieldDescriptor<String, String, K>(
    fieldClass = String::class,
    key = key,
    label = label,
    toolTip = toolTip,
    visible = visible,
    readOnly = readOnly,
    options = { listOf() },
    keyFactory = StringKeyFactory
)
