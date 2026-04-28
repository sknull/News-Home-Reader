package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.KeyFactory
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

/**
 * Describes all aspects to render a configuration field.
 */
abstract class AbstractFieldDescriptor<V : Any, S : Any, K : FieldKey<K>>(

    /** The class of this field (can also be calloction class). */
    val fieldClass: KClass<V>,

    /** Sometimes we need to know the item class of a collection as this is erased on runtime from the fieldclass. */
    val itemClass: KClass<S>? = null,

    /** The primary key of this field. */
    val key: K,

    /** A string resource id used to render the label in th UI. */
    val label: StringResource,

    /** A string resource id used to render a tooltip in the UI (if any). */
    val toolTip: StringResource? = null,

    /**
     * Determines whether the field should be rendered in the UI at all.
     * Sometimes we want to have a configuration which we need to treat separately from the other fields.
     */
    val visible: Boolean = true,

    /** Determines whether this field can be edited or not. */
    val readOnly: Boolean = false,

    /**
     *  For fields which are represented by a combobox or editable list this should generate the
     *  available values rendered in the UI.
     */
    var options: (AbstractConfiguration<*, *>) -> List<Triple<String, UiText?, DrawableResource?>> = { listOf() },

    /** A factory class which handles conversion to and from string values. */
    val keyFactory: KeyFactory<V>
) {
    abstract fun copy(): AbstractFieldDescriptor<V, S, K>
}
