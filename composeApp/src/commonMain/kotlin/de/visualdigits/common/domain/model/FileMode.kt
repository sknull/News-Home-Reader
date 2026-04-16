package de.visualdigits.common.domain.model

import javax.swing.JFileChooser

enum class FileMode(
    val jFileChooserMode: Int
) {

    FILES_ONLY(JFileChooser.FILES_ONLY),

    DIRECTORIES_ONLY(JFileChooser.DIRECTORIES_ONLY)
}
