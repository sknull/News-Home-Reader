package de.visualdigits.newshomereader.domain.model.newsfeedconfiguration

import androidx.compose.runtime.Immutable
import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.FieldsInitializer
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ReferenceListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.StringKeyFactory
import de.visualdigits.common.domain.model.configuration.keyfactory.StringListKeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_feedName
import de.visualdigits.compose.resources.label_imageUrl
import de.visualdigits.compose.resources.label_maingroupName
import de.visualdigits.compose.resources.label_stopWords
import de.visualdigits.compose.resources.label_subgroupName
import de.visualdigits.compose.resources.label_url
import de.visualdigits.compose.resources.tooltip_feedName
import de.visualdigits.compose.resources.tooltip_imageUrl
import de.visualdigits.compose.resources.tooltip_stopWords
import de.visualdigits.compose.resources.tooltip_url
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedItem
import org.jetbrains.compose.resources.DrawableResource

@Immutable
class NewsFeedConfiguration(
    newFields: List<Field<*,*,NC>>? = null,
    val newsFeedGroups: List<NewsFeedGroup>,
): AbstractConfiguration<NewsFeedConfiguration, NC>(newFields?:setupFields()) {

    val mainNewsFeedGroupsMap: Map<String, NewsFeedGroup> = newsFeedGroups.associateBy { nfg -> nfg.name }

    val subNewsFeedGroupsMap: Map<String, Map<String, NewsFeedGroup>> = newsFeedGroups.associate { nfg ->
        Pair(nfg.name, nfg.subGroups.associateBy { sg -> sg.name })
    }

    val newsFeedItemsMap: Map<String, Map<String, Map<String, NewsFeedItem>>> = newsFeedGroups.associate { ng ->
        Pair(ng.name, ng.subGroups.associate { sg ->
            Pair(sg.name, sg.newsFeeds.associateBy { f -> f.name!! }
            )
        })
    }

    companion object : FieldsInitializer<NC> {
        override fun setupFields(): List<Field<*, *, NC>> {
            return listOf(
                Field(
                    descriptor = StringFieldDescriptor(
                        key = NC.feedName,
                        label = UiText.StringResourceId(Res.string.label_feedName),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_feedName)
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                Field(
                    descriptor = ReferenceListFieldDescriptor(
                        fieldClass = String::class,
                        key = NC.mainGroupName,
                        label =  UiText.StringResourceId(Res.string.label_maingroupName),
                        keyFactory = StringKeyFactory,
                        options = { configuration ->
                            (configuration as? NewsFeedConfiguration)?.newsFeedGroups
                                ?.map { nfg -> Triple<String, UiText?, DrawableResource?>(nfg.name, null, null) }
                                ?.sortedBy { t -> t.first }
                                ?:listOf()
                        }
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                Field(
                    descriptor = ReferenceListFieldDescriptor(
                        fieldClass = String::class,
                        key = NC.subGroupName,
                        label =  UiText.StringResourceId(Res.string.label_subgroupName),
                        keyFactory = StringKeyFactory,
                        options = { configuration ->
                            val field = configuration.lookupMap[NC.mainGroupName]
                            (configuration as? NewsFeedConfiguration)?.newsFeedItemsMap[field?.value]
                                ?.keys
                                ?.map { nfg -> Triple<String, UiText?, DrawableResource?>(nfg, null, null) }
                                ?.sortedBy { t -> t.first }
                                ?:listOf()
                        }
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                Field(
                    descriptor = StringFieldDescriptor(
                        key = NC.url,
                        label =  UiText.StringResourceId(Res.string.label_url),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_url)
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                Field(
                    descriptor = StringFieldDescriptor(
                        key = NC.imageUrl,
                        label =  UiText.StringResourceId(Res.string.label_imageUrl),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_imageUrl)
                    ),
                    valid = { value ->
                        (value as? String)?.isNotBlank() == true
                    }
                ),

                Field(
                    descriptor = ListFieldDescriptor(
                        fieldClass = String::class,
                        key = NC.stopWords,
                        label =  UiText.StringResourceId(Res.string.label_stopWords),
                        toolTip =  UiText.StringResourceId(Res.string.tooltip_stopWords),
                        keyFactory = StringListKeyFactory,
                    ),
                    valid = { _ -> true }
                ),
            )
        }
    }

    override fun createInstance(newFields: List<Field<*, *, NC>>): NewsFeedConfiguration {
        return NewsFeedConfiguration(newFields, newsFeedGroups)
    }
}
