package de.visualdigits.newshomereader.presentation.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.domain.model.color.HsvColor
import de.visualdigits.common.domain.model.common.KmpOffsetDateTime
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.ok
import de.visualdigits.generated.AppVersion
import de.visualdigits.newshomereader.domain.model.settings.SK
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.BUTTON_COLOR_DEFAULT
import de.visualdigits.newshomereader.presentation.style.gap
import org.jetbrains.compose.resources.stringResource


@Composable
fun InfoPage(
    state: NewsHomeReaderState,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val buttonColor = remember { (state.settings?.get<HsvColor>(SK.buttonColor) ?: BUTTON_COLOR_DEFAULT).toComposeColor() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.small)
            .padding(MaterialTheme.shapes.gap)
    ) {
        val linkColor = MaterialTheme.colorScheme.onSurface
        val html = remember(linkColor) {
            htmlToAnnotatedString(
                html = """
                        <h1>News Home Reader</h1>
                        <h3>Version ${AppVersion().version}</h3>
                        <br/>
                        <div>© ${KmpOffsetDateTime.now().year} by <a href=\"mailto.s.knull@t-online.de\">Stephan Knull</a>.<div>
                        """.trimIndent(),
                style = HtmlStyle(
                    textLinkStyles = TextLinkStyles(style = SpanStyle(color = linkColor)),
                    isTextColorEnabled = true
                ),
                linkInteractionListener = { linkAnnotation -> uriHandler.openUri((linkAnnotation as LinkAnnotation.Url).url) }
            )
        }

        Text(
            modifier = Modifier
                .padding(16.dp),
            text = html,
            style = MaterialTheme.typography.bodyMedium
        )

        IndicatorButton(
            modifier = Modifier,
            text = stringResource(Res.string.ok),
            buttonColor = buttonColor,
            width = 50.dp,
            height = 50.dp,
        ) {
            onAction(NewsHomeReaderAction.OnShowInfosClick(false))
        }
    }
}
