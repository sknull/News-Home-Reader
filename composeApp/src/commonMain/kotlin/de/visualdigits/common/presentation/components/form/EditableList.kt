package de.visualdigits.common.presentation.components.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.AbstractFieldDescriptor
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbar
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.add
import de.visualdigits.compose.resources.add_hint
import de.visualdigits.compose.resources.cancel
import de.visualdigits.compose.resources.delete
import de.visualdigits.compose.resources.edit
import de.visualdigits.compose.resources.icon_add_24px
import de.visualdigits.compose.resources.icon_cancel_24px
import de.visualdigits.compose.resources.icon_delete_24px
import de.visualdigits.compose.resources.icon_edit_24px
import de.visualdigits.compose.resources.icon_file_save_24px
import de.visualdigits.compose.resources.ok
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.util.conditional
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditableList(
    modifier: Modifier = Modifier,
    configuration: AbstractConfiguration<*,*>?,
    field: Field<MutableList<*>,*,*>,
    fieldHeight: Dp = Dp.Unspecified,
    space: Dp,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    buttonShape: Shape = MaterialTheme.shapes.extraSmall,
    containerShape: Shape = MaterialTheme.shapes.small,
    buttonColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    textStyle: TextStyle,
    scrollable: Boolean = false,
    onValueChange: (KeyValue) -> Unit,
    deleteAllowed: (AbstractFieldDescriptor<*,*,*>, String) -> Boolean = { _, _ -> true }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val values = (field.value as? List<String>)?:listOf()
    val previousItems = remember { values.toMutableStateList() }
    val items = remember { mutableStateListOf<String>() }
    LaunchedEffect(values) {
        if (items != values) {
            items.clear()
            items.addAll(values)
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var currentText by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.shapes.gap)
            .border(1.dp, unfocusedBorderColor, buttonShape)
    ) {
        val scrollState = rememberScrollState(0)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .conditional(scrollable) { verticalScroll(scrollState) },
            verticalArrangement = Arrangement.spacedBy(space)
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(field.descriptor.label),
                style = MaterialTheme.typography.bodySmall,
            )

            items.forEachIndexed { index, item ->
                val allowDelete = deleteAllowed(field.descriptor, item)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(fieldHeight * 0.75f),
                    shape = buttonShape,
                    color = Color.Transparent,
                    border = BorderStroke(
                        width = 1.dp,
                        color = unfocusedBorderColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(start = MaterialTheme.shapes.gap, end = 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(Modifier.weight(1f))

                        if (field.enabled) {
                            IndicatorButton(
                                leadingIcon = painterResource(Res.drawable.icon_edit_24px),
                                toolTip = stringResource(Res.string.edit),
                                width = 30.dp,
                                height = 30.dp,
                                onClick = {
                                    editingIndex = index
                                    currentText = item
                                    showDialog = true
                                }
                            )

                            IndicatorButton(
                                leadingIcon = painterResource(Res.drawable.icon_delete_24px),
                                toolTip = stringResource(Res.string.delete),
                                width = 30.dp,
                                height = 30.dp,
                                enabled = allowDelete,
                                onClick = {
                                    editingIndex = null
                                    currentText = null
                                    items.removeAt(index)
                                    showDialog = false
                                    onValueChange(KeyValue(field.descriptor, items.joinToString(",")))
                                }
                            )
                        }
                    }
                }
            }

            if (field.enabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IndicatorButton(
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                        text = stringResource(Res.string.add_hint),
                        buttonColor = buttonColor,
                        shape = buttonShape,
                        leadingIcon = painterResource(Res.drawable.icon_add_24px),
                        leadingIconTint = iconTint
                    ) {
                        editingIndex = null
                        currentText = ""
                        showDialog = true
                    }
                }
            }
        }

        if (scrollable) {
            PlatformVerticalScrollbar(
                interactionSource = interactionSource,
                modifier = Modifier
                    .clip(containerShape)
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f))
                    .width(MaterialTheme.shapes.gap),
                scrollState = scrollState
            )
        }
    }

    if (showDialog) {
        previousItems.update(items)

        AlertDialog(
            modifier = Modifier
                .border(1.dp, focusedBorderColor, containerShape),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.6f),
            shape = containerShape,
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingIndex == null) stringResource(Res.string.add) else stringResource(Res.string.edit)) },
            text = {
                TypeAwareEditableField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    configuration = configuration,
                    field = field,
                    currentValue = currentText,
                    fieldHeight = fieldHeight,
                    focusedBorderColor = focusedBorderColor,
                    unfocusedBorderColor = unfocusedBorderColor,
                    textStyle = textStyle,
                    iconTint = iconTint,
                    buttonShape = buttonShape,
                    buttonColor = buttonColor,
                    enabled = field.enabled,
                    onValueChange = { keyValue ->
                        currentText = keyValue.value ?: ""
                    },
                    hasFocus = true
                )
            },
            confirmButton = {
                IndicatorButton(
                    text = stringResource(Res.string.ok),
                    buttonColor = buttonColor,
                    shape = buttonShape,
                    leadingIcon = painterResource(Res.drawable.icon_file_save_24px),
                    leadingIconTint = iconTint
                ) {
                    val previousValue = editingIndex?.let { i -> items[i] }
                    if (editingIndex != null) {
                        currentText?.also { ct -> items[editingIndex!!] = ct }

                    } else {
                        currentText?.also { ct -> items.add(ct) }
                    }
                    onValueChange(
                        KeyValue(
                            descriptor = field.descriptor,
                            value = if (items.isNotEmpty()) items.joinToString(",") else null,
                            previousValue = previousValue,
                            newValue = currentText
                        )
                    )
                    showDialog = false
                }
            },
            dismissButton = {
                IndicatorButton(
                    text = stringResource(Res.string.cancel),
                    buttonColor = buttonColor,
                    shape = buttonShape,
                    leadingIcon = painterResource(Res.drawable.icon_cancel_24px),
                    leadingIconTint = iconTint
                ) {
                    items.update(previousItems)
                    onValueChange(KeyValue(field.descriptor, items.joinToString(",")))
                    showDialog = false
                }
            }
        )
    }
}

private fun <T> SnapshotStateList<T>.update(values: MutableList<T>) {
    clear()
    addAll(values)
}
