package de.visualdigits.newshomereader.presentation.page.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.IndicatorButton
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.icon_add_notes_24px
import de.visualdigits.compose.resources.icon_docs_add_on_24px
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderAction
import de.visualdigits.newshomereader.presentation.model.NewsHomeReaderState
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditButtonsBottom(
    modifier: Modifier = Modifier,
    state: NewsHomeReaderState,
    onAction: (NewsHomeReaderAction) -> Unit,
    newsFeedGroup: NewsFeedGroup
) {
    if (state.isEditMode) {
        Row(
            modifier = modifier,
        ) {
            // only allow one sub group level
            if (newsFeedGroup.parentGroupName == null) {
                IndicatorButton(
                    modifier = Modifier,
                    width = 30.dp,
                    height = 30.dp,
                    padding = 2.dp,
                    leadingIcon = painterResource(Res.drawable.icon_add_notes_24px)
                ) {
                    onAction(NewsHomeReaderAction.OnAddNewsfeedGroupGroupClick(
                        newsFeedGroup = newsFeedGroup
                    ))
                }
            }

            IndicatorButton(
                modifier = Modifier,
                width = 30.dp,
                height = 30.dp,
                padding = 2.dp,
                leadingIcon = painterResource(Res.drawable.icon_docs_add_on_24px)
            ) {
                onAction(NewsHomeReaderAction.OnAddNewsFeedConfigurationClick(newsFeedGroup = newsFeedGroup))
            }
        }
    }
}
