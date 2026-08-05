package de.visualdigits.newshomereader.presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.form.EditableListResources
import de.visualdigits.common.domain.model.form.FileChooserResources
import de.visualdigits.common.domain.model.form.FormFieldResources
import de.visualdigits.common.domain.model.form.FormResources
import de.visualdigits.common.domain.model.form.LocalEditableListResources
import de.visualdigits.common.domain.model.form.LocalFileChooserResources
import de.visualdigits.common.domain.model.form.LocalFormFieldResources
import de.visualdigits.common.domain.model.form.LocalFormResources
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.presentation.components.util.LocalSwitchColors
import de.visualdigits.common.presentation.components.util.switchBoxColors
import de.visualdigits.common.presentation.model.LocalPlatformScrollbarStyle
import de.visualdigits.common.presentation.model.PlatformScrollbarStyle
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.add
import de.visualdigits.compose.resources.add_hint
import de.visualdigits.compose.resources.cancel
import de.visualdigits.compose.resources.delete
import de.visualdigits.compose.resources.edit
import de.visualdigits.compose.resources.icon_add_24px
import de.visualdigits.compose.resources.icon_cancel_24px
import de.visualdigits.compose.resources.icon_check_small_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.compose.resources.icon_folder_open_24px
import de.visualdigits.compose.resources.icon_visibility_24px
import de.visualdigits.compose.resources.ok
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppCompositionProvider(
    content: @Composable () -> Unit
) {
    val platformScrollbarStyle = PlatformScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 8.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 300,
        unhoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        hoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    )
    val formResource = FormResources(
        backgroundColor = Color.Transparent,
        buttonShape = MaterialTheme.shapes.extraSmall,
        iconOk = painterResource(Res.drawable.icon_check_small_24px),
        tooltipOk = UiText.StringResourceId(Res.string.ok),
        iconCancel = painterResource(Res.drawable.icon_cancel_24px),
        tooltipCancel = UiText.StringResourceId(Res.string.cancel),
        buttonColor = Color.Black,
        containerShape = MaterialTheme.shapes.small,
    )
    val formFieldResources = FormFieldResources(
        fieldHeight = 50.dp,
        textStyle = MaterialTheme.typography.bodyMedium,
        iconTint = Color.White,
        shape = MaterialTheme.shapes.extraSmall,
        focusedBorderColor = MaterialTheme.colorScheme.outline,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
        visibilityIcon = painterResource(Res.drawable.icon_visibility_24px)
    )
    val fileChooserResources = FileChooserResources(
        iconFolder = painterResource(Res.drawable.icon_folder_open_24px),
        iconTint = MaterialTheme.colorScheme.onSurface,
        buttonColor =MaterialTheme.colorScheme.surface,
        titleDirectories = "Choose Directory",
        titleFiles = "Choose File",
    )
    val editableListResources = EditableListResources(
        tooltipAdd = UiText.StringResourceId(Res.string.add_hint),
        titleAdd = UiText.StringResourceId(Res.string.add),
        iconAdd = Res.drawable.icon_add_24px,
        titleEdit = UiText.StringResourceId(Res.string.edit),
        iconEdit = Res.drawable.icon_edit_24px,
        toolTipDelete = UiText.StringResourceId(Res.string.delete),
        iconDelete = Res.drawable.icon_delete_24px,
        toolTipEdit = UiText.StringResourceId(Res.string.edit),
        labelOk = UiText.StringResourceId(Res.string.ok),
        iconOk = Res.drawable.icon_check_small_24px,
        labelCancel = UiText.StringResourceId(Res.string.cancel),
        iconCancel = Res.drawable.icon_cancel_24px
    )
    val switchColors = switchBoxColors()

    CompositionLocalProvider(
        LocalFormResources provides formResource,
        LocalFormFieldResources provides formFieldResources,
        LocalEditableListResources provides editableListResources,
        LocalPlatformScrollbarStyle provides platformScrollbarStyle,
        LocalFileChooserResources provides fileChooserResources,
        LocalSwitchColors provides switchColors
    ) {
        content()
    }
}
