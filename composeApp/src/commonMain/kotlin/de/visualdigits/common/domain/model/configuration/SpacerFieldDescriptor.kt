package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.configuration.keyfactory.StringKeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.field_unset


/**
 * Represents a field which should provide a file or directory picker.
 */
class SpacerFieldDescriptor<K : FieldKey<K>>(
    key: K
): AbstractFieldDescriptor<String, String, K>(
    fieldClass = String::class,
    key = key,
    label = Res.string.field_unset,
    toolTip = Res.string.field_unset,
    visible = true,
    readOnly = true,
    options = { listOf() },
    keyFactory = StringKeyFactory
)
