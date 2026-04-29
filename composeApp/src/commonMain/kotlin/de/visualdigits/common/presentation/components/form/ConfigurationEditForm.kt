package de.visualdigits.common.presentation.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.color
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.AbstractFieldDescriptor
import de.visualdigits.common.domain.model.configuration.FieldKey
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.SpacerFieldDescriptor
import de.visualdigits.common.domain.model.form.EditableListResources
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.common.presentation.model.CommonAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationEditForm(
    modifier: Modifier = Modifier,
    titleChooseDirectory: UiText,
    titleChooseFile: UiText,
    iconFolder: Painter,
    editableListResources: EditableListResources,
    tooltipOk: UiText,
    iconOk: Painter,
    tooltipCancel: UiText,
    iconCancel: Painter,
    scrollPosition: MutableMap<String, Pair<Int, Int?>>,
    scrollbarId: String,
    fieldHeight: Dp = Dp.Unspecified,
    focusedBorderColor: Color = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    buttonShape: Shape = MaterialTheme.shapes.extraSmall,
    buttonColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    containerShape: Shape = MaterialTheme.shapes.small,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    space: Dp = 8.dp,
    onValueChange: (KeyValue) -> Unit,
    configuration: () -> AbstractConfiguration<*,*>,
    collapsibleState: Map<String, Boolean>,
    onCancelClick: () -> Unit,
    onOkClick: () -> Unit,
    onCommonAction: (CommonAction) -> Unit,
    deleteAllowed: (AbstractFieldDescriptor<*,*,*>?, String) -> Boolean = { _,_ -> true }
) {
    PlatformVerticalScrollbarBox(
        boxModifier = modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
        scrollbarModifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .fillMaxHeight()
            .width(10.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
        scrollbarId,
        scrollPosition = scrollPosition,
        collapsibleState = collapsibleState,
        onCommonAction = onCommonAction
    ) {
        listOf(
            Pair("fields", @Composable {
                FlowRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(space),
                    verticalArrangement = Arrangement.spacedBy(space)
                ) {
                    val configuration1 = configuration()
                    val fields = configuration1
                        .fields
                    val filter = fields
                        .filter { (_, field) -> field.descriptor.visible }
                    val values = filter
                        .values
                    values
                        .forEach { field ->
                            Box(
                                modifier = Modifier
                                    .width(300.dp)
                            ) {
                                EditableField(
                                    configuration = configuration1,
                                    titleChooseDirectory = titleChooseDirectory,
                                    titleChooseFile = titleChooseFile,
                                    iconFolder = iconFolder,
                                    editableListResources = editableListResources,
                                    fieldKey = field.descriptor.key,
                                    fieldHeight = fieldHeight,
                                    space = space,
                                    unfocusedBorderColor = unfocusedBorderColor,
                                    focusedBorderColor = focusedBorderColor,
                                    iconTint = iconTint,
                                    buttonColor = buttonColor,
                                    buttonShape = buttonShape,
                                    containerShape = containerShape,
                                    textStyle = textStyle,
                                    onValueChange = onValueChange,
                                    deleteAllowed = deleteAllowed
                                )
                            }
                        }
                }
            }),
            Pair("spacer", @Composable {
                Spacer(Modifier.height(16.dp))
            }),
            Pair("buttons", @Composable {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space),
                    modifier = Modifier
                        .wrapContentWidth(),
                ) {
                    Spacer(Modifier.weight(1f))

                    IndicatorButton(
                        toolTip = tooltipCancel.asString(),
                        width = 50.dp,
                        height = 50.dp,
                        buttonColor = buttonColor,
                        shape = buttonShape,
                        leadingIcon = iconCancel,
                        leadingIconTint = iconTint,
                        onClick = onCancelClick
                    )

                    IndicatorButton(
                        toolTip = tooltipOk.asString(),
                        width = 50.dp,
                        height = 50.dp,
                        buttonColor = buttonColor,
                        shape = buttonShape,
                        leadingIcon = iconOk,
                        leadingIconTint = iconTint,
                        onClick = onOkClick
                    )
                }
            })
        )
    }
}

@Composable
private fun EditableField(
    configuration: AbstractConfiguration<*,*>,
    titleChooseDirectory: UiText,
    titleChooseFile: UiText,
    iconFolder: Painter,
    editableListResources: EditableListResources,
    fieldKey: FieldKey<*>,
    fieldHeight: Dp,
    space: Dp,
    unfocusedBorderColor: Color,
    focusedBorderColor: Color,
    iconTint: Color,
    buttonColor: Color,
    buttonShape: Shape,
    containerShape: Shape,
    textStyle: TextStyle,
    onValueChange: (KeyValue) -> Unit,
    deleteAllowed: (AbstractFieldDescriptor<*,*,*>?, String) -> Boolean
) {
    val field = configuration.fields[fieldKey]
    val isEditable = !(field?.descriptor?.readOnly?:false)
    if (field?.valid(field.value) == true) Color.Unspecified else Severity.Error.color()

    when(field?.descriptor) {
        is ListFieldDescriptor -> {
            EditableList(
                titleChooseDirectory = titleChooseDirectory,
                titleChooseFile = titleChooseFile,
                iconFolder = iconFolder,
                resources = editableListResources,
                configuration = configuration,
                fieldKey = fieldKey,
                fieldHeight = fieldHeight,
                space = space,
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = unfocusedBorderColor,
                iconTint = iconTint,
                buttonShape = buttonShape,
                containerShape = containerShape,
                buttonColor = buttonColor,
                textStyle = textStyle,
                onValueChange = onValueChange,
                deleteAllowed = deleteAllowed
            )
        }

        is SpacerFieldDescriptor ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

            }

        else -> {
            TypeAwareEditableField(
                modifier = Modifier
                    .fillMaxWidth(),
                titleChooseDirectory = titleChooseDirectory,
                titleChooseFile = titleChooseFile,
                iconFolder = iconFolder,
                configuration = configuration,
                fieldKey = fieldKey,
                fieldHeight = fieldHeight,
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = unfocusedBorderColor,
                textStyle = textStyle,
                iconTint = iconTint,
                buttonShape = buttonShape,
                buttonColor = buttonColor,
                enabled = isEditable,
                onValueChange = onValueChange
            )
        }
    }
}

