package de.visualdigits.newshomereader.domain.model.newsfeedconfiguration

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ReferenceListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.StringKeyFactory
import de.visualdigits.common.domain.model.configuration.keyfactory.StringListKeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_feedName
import de.visualdigits.compose.resources.label_maingroupName
import de.visualdigits.compose.resources.label_stopWords
import de.visualdigits.compose.resources.label_subgroupName
import de.visualdigits.compose.resources.label_url
import de.visualdigits.compose.resources.tooltip_feedName
import de.visualdigits.compose.resources.tooltip_stopWords
import de.visualdigits.compose.resources.tooltip_url
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import org.jetbrains.compose.resources.DrawableResource

@Immutable
class NewsFeedConfiguration(
    values: Map<NC, Any?> = mapOf(),
    val newsFeedGroups: List<NewsFeedGroup>,
): AbstractConfiguration<NewsFeedConfiguration, NC>(values, DESCRIPTORS) {

    val mainNewsFeedGroupsMap: Map<String, NewsFeedGroup> = newsFeedGroups.associateBy { nfg -> nfg.name }

    val newsFeedItemsMap: Map<String, Map<String, Map<String, NewsFeedItem>>> = newsFeedGroups.associate { ng ->
        Pair(ng.name, ng.subGroups.associate { sg ->
            Pair(sg.name, sg.newsFeeds.associateBy { f -> f.name!! }
            )
        })
    }

    companion object {
        val DESCRIPTORS = listOf(
            StringFieldDescriptor(
                key = NC.feedName,
                label = UiText.StringResourceId(Res.string.label_feedName),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_feedName),
                valid = { _, value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),

            ReferenceListFieldDescriptor(
                fieldClass = String::class,
                key = NC.mainGroupName,
                label =  UiText.StringResourceId(Res.string.label_maingroupName),
                keyFactory = StringKeyFactory,
                options = { configuration, _ ->
                    (configuration as? NewsFeedConfiguration)?.newsFeedGroups
                        ?.map { nfg -> Triple<String, UiText?, DrawableResource?>(nfg.name, null, null) }
                        ?.sortedBy { t -> t.first }
                        ?:listOf()
                },
                valid = { _, value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),

            ReferenceListFieldDescriptor(
                fieldClass = String::class,
                key = NC.subGroupName,
                label =  UiText.StringResourceId(Res.string.label_subgroupName),
                keyFactory = StringKeyFactory,
                options = { configuration, _ ->
                    (configuration as? NewsFeedConfiguration)?.newsFeedItemsMap[configuration.values[NC.mainGroupName]]
                        ?.keys
                        ?.map { nfg -> Triple<String, UiText?, DrawableResource?>(nfg, null, null) }
                        ?.sortedBy { t -> t.first }
                        ?:listOf()
                }
            ),

            StringFieldDescriptor(
                key = NC.url,
                label =  UiText.StringResourceId(Res.string.label_url),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_url),
                valid = { _, value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),

            ListFieldDescriptor(
                fieldClass = String::class,
                key = NC.stopWords,
                label =  UiText.StringResourceId(Res.string.label_stopWords),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_stopWords),
                keyFactory = StringListKeyFactory,
                valid = { _, _ -> true }
            ),
        )
    }

    override fun createInstance(newValues: Map<NC, Any?>): NewsFeedConfiguration {
        return NewsFeedConfiguration(newValues, newsFeedGroups)
    }
}
