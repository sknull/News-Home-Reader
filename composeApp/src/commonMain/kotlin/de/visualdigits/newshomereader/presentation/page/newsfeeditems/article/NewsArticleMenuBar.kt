package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_arrow_back_24px
import de.visualdigits.compose.resources.icon_chat_24px
import de.visualdigits.compose.resources.icon_link_24px
import de.visualdigits.compose.resources.tooltip_back
import de.visualdigits.compose.resources.tooltip_open_chat
import de.visualdigits.compose.resources.tooltip_open_link
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.style.gap
import de.visualdigits.newshomereader.presentation.util.makeUrlAbsolute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewsArticleMenuBar(
    newsItem: NewsItem,
    newsArticle: FullArticle,
    uriHandler: UriHandler,
    onAction: (NewsHomeReaderAction) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(5.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicatorButton(
            modifier = Modifier,
            width = 50.dp,
            height = 50.dp,
            leadingIcon = painterResource(Res.drawable.icon_arrow_back_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface,
            toolTip = stringResource(Res.string.tooltip_back),
        ) {
            onAction(NewsHomeReaderAction.OnNewsItemBackClicked())
        }

        IndicatorButton(
            modifier = Modifier,
            width = 50.dp,
            height = 50.dp,
            leadingIcon = painterResource(Res.drawable.icon_link_24px),
            leadingIconTint = MaterialTheme.colorScheme.onSurface,
            toolTip = stringResource(Res.string.tooltip_open_link),
        ) {
            uriHandler.openUri(newsItem.link)
        }

        newsArticle.discussionUrl?.also { link ->
            IndicatorButton(
                modifier = Modifier,
                width = 50.dp,
                height = 50.dp,
                leadingIcon = painterResource(Res.drawable.icon_chat_24px),
                leadingIconTint = MaterialTheme.colorScheme.onSurface,
                toolTip = stringResource(Res.string.tooltip_open_chat),
            ) {
                uriHandler.openUri(
                    makeUrlAbsolute(
                        newsItem.link,
                        link
                    )
                )
            }
        }
    }
}
