package de.visualdigits.newshomereader.domain.model.newsfeedconfiguration

import androidx.compose.runtime.Immutable
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ReferenceListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.BooleanEnum
import de.visualdigits.common.domain.model.configuration.keyfactory.StringKeyFactory
import de.visualdigits.common.domain.model.configuration.keyfactory.StringListKeyFactory
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.group_group
import de.visualdigits.compose.resources.group_name
import de.visualdigits.compose.resources.group_settings
import de.visualdigits.compose.resources.label_feedName
import de.visualdigits.compose.resources.label_is_keyword_bucket
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
            EnumFieldDescriptor(
                fieldClass = BooleanEnum::class,
                group = UiText.StringResourceId(Res.string.group_name),
                key = NC.isKeyword,
                label =  UiText.StringResourceId(Res.string.label_is_keyword_bucket),
                toolTip =  UiText.StringResourceId(Res.string.label_is_keyword_bucket),
                options = { _, _ -> BooleanEnum.options },
                keyFactory = BooleanEnum,
                default = BooleanEnum.FALSE
            ),

            StringFieldDescriptor(
                key = NC.feedName,
                group = UiText.StringResourceId(Res.string.group_name),
                label = UiText.StringResourceId(Res.string.label_feedName),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_feedName),
            ),

            ReferenceListFieldDescriptor(
                fieldClass = String::class,
                group = UiText.StringResourceId(Res.string.group_group),
                key = NC.mainGroupName,
                label =  UiText.StringResourceId(Res.string.label_maingroupName),
                keyFactory = StringKeyFactory,
                options = { configuration, _ ->
                    (configuration as? NewsFeedConfiguration)?.newsFeedGroups
                        ?.map { nfg -> Triple<String, UiText?, DrawableResource?>(nfg.name, null, null) }
                        ?.sortedBy { t -> t.first }
                        ?:listOf()
                },
            ),

            ReferenceListFieldDescriptor(
                fieldClass = String::class,
                group = UiText.StringResourceId(Res.string.group_group),
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
                group = UiText.StringResourceId(Res.string.group_settings),
                key = NC.url,
                label =  UiText.StringResourceId(Res.string.label_url),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_url),
//                enabledCondition = { configuration, _ -> configuration.get<BooleanEnum>(NC.isKeyword)?.booleanValue != true },
                valid = { configuration, value ->
                    if ((value as? String)?.isNotBlank() == true || configuration.get<BooleanEnum>(NC.isKeyword)?.booleanValue == true) {
                        Severity.Info
                    } else {
                        Severity.Error
                    }
                }
            ),

            ListFieldDescriptor(
                fieldClass = String::class,
                group = UiText.StringResourceId(Res.string.group_settings),
                key = NC.stopWords,
                label =  UiText.StringResourceId(Res.string.label_stopWords),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_stopWords),
                keyFactory = StringListKeyFactory,
//                enabledCondition = { configuration, _ -> configuration.get<BooleanEnum>(NC.isKeyword)?.booleanValue != true },
            ),
        )
    }

    override fun createInstance(newValues: Map<NC, Any?>): NewsFeedConfiguration {
        return NewsFeedConfiguration(newValues, newsFeedGroups)
    }
}
