package de.visualdigits.newshomereader.presentation.page.newsfeeditems.article

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.visualdigits.newshomereader.domain.model.unified.FullArticle
import de.visualdigits.newshomereader.domain.model.unified.NewsItem
import de.visualdigits.newshomereader.presentation.page.newsfeeditems.Image
import de.visualdigits.newshomereader.presentation.style.gap

@Composable
fun ArticleImage(
    modifier: Modifier = Modifier,
    newsItem: NewsItem,
    newsArticle: FullArticle?,
    maxImageSize: Int?
) {
    var image = newsArticle?.articleImage
    if (image == null || image.isEmpty()) {
        image = newsItem.image
    }
    if (image.isNotEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.shapes.gap)
        ) {
            if (newsItem.imageTitle.isNotEmpty()) {
                Text(
                    text = newsItem.imageTitle,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Image(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small),
                url = image,
                contentDescription = newsItem.imageCaption,
                maxImageSize = maxImageSize
            )

            if (newsItem.imageCaption.isNotEmpty() && !newsItem.summary.contains(newsItem.imageCaption)) {
                Text(
                    text = newsItem.imageCaption,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
