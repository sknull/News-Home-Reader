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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.KeyValue
import de.visualdigits.common.domain.model.color
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.AbstractFieldDescriptor
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.SpacerFieldDescriptor
import de.visualdigits.common.presentation.components.PlatformVerticalScrollbarBox
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.cancel
import de.visualdigits.compose.resources.icon_cancel_24px
import de.visualdigits.compose.resources.icon_check_small_24px
import de.visualdigits.compose.resources.ok
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationEditForm(
    modifier: Modifier = Modifier,
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
    space: Dp = MaterialTheme.shapes.gap,
    onValueChange: (KeyValue) -> Unit,
    configuration: AbstractConfiguration<*,*>?,
    onCancelClick: () -> Unit,
    onOkClick: () -> Unit,
    onAction: (NewsHomeReaderAction) -> Unit,
    deleteAllowed: (AbstractFieldDescriptor<*,*,*>, String) -> Boolean = { _,_ -> true }
) {
    PlatformVerticalScrollbarBox(
        boxModifier = modifier
            .fillMaxWidth(),
        scrollbarModifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .fillMaxHeight()
            .width(10.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f)),
        scrollbarId,
        scrollPosition = scrollPosition,
        onAction
    ) {
        listOf(
            Pair("fields", @Composable {
                FlowRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(space),
                    verticalArrangement = Arrangement.spacedBy(space)
                ) {
                    configuration
                        ?.fields
                        ?.filter { (_, field) -> field.descriptor.visible }
                        ?.values
                        ?.forEach { field ->
                            Box(
                                modifier = Modifier
                                    .width(300.dp)
                            ) {
                                EditableField(
                                    configuration = configuration,
                                    field = field,
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
                        toolTip = stringResource(Res.string.cancel),
                        width = 50.dp,
                        height = 50.dp,
                        buttonColor = buttonColor,
                        shape = buttonShape,
                        leadingIcon = painterResource(Res.drawable.icon_cancel_24px),
                        leadingIconTint = iconTint,
                        onClick = onCancelClick
                    )

                    IndicatorButton(
                        toolTip = stringResource(Res.string.ok),
                        width = 50.dp,
                        height = 50.dp,
                        buttonColor = buttonColor,
                        shape = buttonShape,
                        leadingIcon = painterResource(Res.drawable.icon_check_small_24px),
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
    field: Field<*,*,*>,
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
    deleteAllowed: (AbstractFieldDescriptor<*,*,*>, String) -> Boolean
) {
    val isEditable = !field.descriptor.readOnly
    if (field.valid(field.value)) Color.Unspecified else Severity.Error.color()

    when(field.descriptor) {
        is ListFieldDescriptor -> {
            EditableList(
                configuration = configuration,
                field = field as Field<MutableList<*>, *, *>,
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
                configuration = configuration,
                field = field,
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

