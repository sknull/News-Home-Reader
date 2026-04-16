package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

/**
 * Represents a field which is rendered as an editable list in the UI.
 */
@Suppress("UNCHECKED_CAST")
open class ListFieldDescriptor<F : Any, K : FieldKey<K>>(
    fieldClass: KClass<F>,

    key: K,

    label: StringResource,
    toolTip: StringResource? = null,

    visible: Boolean = true,
    readOnly: Boolean = false,

    options: () -> List<Triple<String, UiText?, DrawableResource?>> = { listOf() },

    keyFactory: KeyFactory<MutableList<F>>
): AbstractFieldDescriptor<MutableList<F>, F, K>(
    fieldClass = MutableList::class as KClass<MutableList<F>>,
    itemClass = fieldClass,
    key = key,
    label = label,
    toolTip = toolTip,
    visible = visible,
    readOnly = readOnly,
    options = options,
    keyFactory = keyFactory
)

