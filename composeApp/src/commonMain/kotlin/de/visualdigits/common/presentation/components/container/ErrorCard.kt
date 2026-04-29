package de.visualdigits.common.presentation.components.container

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.color


@Composable
fun ErrorCard(
    errorMessage: UiText?,
    severity: Severity? = Severity.Error,
    shapeContainer: Shape,
    space: Dp = 8.dp
) {
    if (errorMessage != null) {
        val color = severity?.color()?:Severity.Error.color()
        Card(
            modifier = Modifier
                .padding(top = space)
                .border(width = 1.dp, color = color, shape = shapeContainer)
                .fillMaxWidth(),
            shape = shapeContainer,
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            )
        ) {
            Text(
                text = errorMessage.asString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                modifier = Modifier
                    .padding(16.dp)
            )
        }

        Spacer(Modifier.height(space).fillMaxWidth())
    }
}
