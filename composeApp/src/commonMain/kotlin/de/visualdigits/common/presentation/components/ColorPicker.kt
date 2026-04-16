package de.visualdigits.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import de.visualdigits.common.domain.model.HsvColor
import de.visualdigits.common.presentation.components.modifier.colorSwatch
import de.visualdigits.newshomereader.presentation.style.gap


@Composable
fun ColorPicker(
    label: String,
    initialColor: HsvColor?,
    size: Dp = 200.dp,
    space: Dp,
    shape: Shape,
    hasSwatch: Boolean = true,
    onColorChanged: (colorEnvelope: ColorEnvelope) -> Unit = {}
) {
    val controller = rememberColorPickerController()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f), shape)
            .width(size)
            .fillMaxHeight()
            .padding(MaterialTheme.shapes.gap),
        verticalArrangement = Arrangement.spacedBy(space),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Start
        )

        val initialColor1 = initialColor?.toColor() ?: Color.Transparent

        HsvColorPicker(
            modifier = Modifier
                .width(size)
                .height(size)
                .padding(10.dp),
            controller = controller,
            initialColor = initialColor1,
            onColorChanged = onColorChanged
        )

        BrightnessSlider(
            modifier = Modifier
                .height(35.dp)
                .padding(10.dp)
                .clip(shape),
            initialColor = initialColor1,
            controller = controller,
        )

        if (hasSwatch) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .colorSwatch(
                        size = 50.dp,
                        selectedColor = initialColor1,
                        controller = controller,
                        shape = shape,
                    )
            ) {}
        }
    }
}
