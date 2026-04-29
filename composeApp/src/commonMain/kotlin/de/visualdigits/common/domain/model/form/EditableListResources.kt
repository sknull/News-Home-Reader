package de.visualdigits.common.domain.model.form

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import de.visualdigits.common.domain.model.UiText

@Immutable
data class EditableListResources(
    val hintAdd: UiText,

    val titleAdd: UiText,
    val iconAdd: Painter,

    val titleEdit: UiText,
    val iconEdit: Painter,

    val toolTipDelete: UiText,
    val iconDelete: Painter,

    val toolTipEdit: UiText,

    val labelOk: UiText,

    val labelCancel: UiText,
    val iconCancel: Painter,

    val iconSaveFile: Painter
)
