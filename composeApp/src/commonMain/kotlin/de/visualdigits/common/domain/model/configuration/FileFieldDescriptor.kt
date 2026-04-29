package de.visualdigits.common.domain.model.configuration

import de.visualdigits.common.domain.model.FileMode
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.keyfactory.FileKeyFactory
import org.jetbrains.compose.resources.DrawableResource
import java.io.File

/**
 * Represents a field which should provide a file or directory picker.
 */
class FileFieldDescriptor<K : FieldKey<K>>(
    key: K,

    label: UiText,
    toolTip: UiText? = null,

    visible: Boolean = true,
    readOnly: Boolean = false,

    val fileMode: FileMode,
    var startDirectory: (AbstractConfiguration<*,*>?) -> File = {
        File(System.getProperty("user.home"))
    },

    options: (AbstractConfiguration<*, *>) -> List<Triple<String, UiText?, DrawableResource?>> = { listOf() },
): AbstractFieldDescriptor<File, File, K>(
    fieldClass = File::class,
    key = key,
    label = label,
    toolTip = toolTip,
    visible = visible,
    readOnly = readOnly,
    options = options,
    keyFactory = FileKeyFactory
)
