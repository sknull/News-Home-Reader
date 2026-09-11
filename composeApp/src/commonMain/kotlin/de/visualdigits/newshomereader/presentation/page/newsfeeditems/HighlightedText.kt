package de.visualdigits.newshomereader.presentation.page.newsfeeditems

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.em
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import de.visualdigits.common.domain.model.color.HsvColor
import de.visualdigits.common.presentation.util.highlightQuery
import de.visualdigits.common.presentation.util.openUriSafely
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.domain.util.StringEscapeUtils.normalizeXml
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.style.textLinkStyles
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute

@Composable
fun HighlightedText(
    html: String,
    spotColor: HsvColor,
    newsItem: NewsItem,
    uriHandler: UriHandler,
    state: NewsHomeReaderState
) {
    val annotatedText = htmlToAnnotatedString(
        html = normalizeXml(html),
        style = HtmlStyle(
            textLinkStyles = textLinkStyles(spotColor)
        ),
        linkInteractionListener = { linkAnnotation ->
            makeUrlAbsolute(
                newsItem.link,
                (linkAnnotation as LinkAnnotation.Url).url
            ).let { uriHandler.openUriSafely(it) }
        }
    )
    val highlightedText = remember(annotatedText, state.newsItemSearchText) {
        if (!state.newsItemSearchText.isNullOrBlank()) {
            annotatedText.highlightQuery(state.newsItemSearchText)
        } else if (!state.currentKeywordBucket.isNullOrBlank()) {
            annotatedText.highlightQuery(state.currentKeywordBucket)
        } else {
            annotatedText
        }
    }
    val lineHeight = if (html.startsWith("<h")) 2.0.em else 1.5.em
    Text(
        modifier = Modifier
            .padding(vertical = MaterialTheme.shapes.gap),
        text = highlightedText,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = lineHeight)
    )
}
