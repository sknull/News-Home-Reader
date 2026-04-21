package de.visualdigits.newshomereader.domain.model.newsfeedconfiguration

import de.visualdigits.common.domain.model.UiText
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.ReferenceListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.StringKeyFactory
import de.visualdigits.common.domain.model.configuration.keyfactory.StringListKeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_feedName
import de.visualdigits.compose.resources.label_groupName
import de.visualdigits.compose.resources.label_imageUrl
import de.visualdigits.compose.resources.label_stopWords
import de.visualdigits.compose.resources.label_url
import de.visualdigits.compose.resources.tooltip_feedName
import de.visualdigits.compose.resources.tooltip_imageUrl
import de.visualdigits.compose.resources.tooltip_stopWords
import de.visualdigits.compose.resources.tooltip_url
import de.visualdigits.newshomereader.domain.model.unified.NewsFeedGroup
import org.jetbrains.compose.resources.DrawableResource

class NewsFeedConfiguration(
    fields: LinkedHashMap<NC, Field<*,*,NC>> = LinkedHashMap(),
    val newsFeedGroups: List<NewsFeedGroup>,
): AbstractConfiguration<NewsFeedConfiguration, NC>(fields) {

    override fun setupFields(): List<Field<*, *, NC>> {
        return listOf(
            Field(
                descriptor = StringFieldDescriptor(
                    key = NC.feedName,
                    label = Res.string.label_feedName,
                    toolTip = Res.string.tooltip_feedName
                ),
                valid = { value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),
            Field(
                descriptor = ReferenceListFieldDescriptor(
                    fieldClass = String::class,
                    key = NC.groupName,
                    label = Res.string.label_groupName,
                    keyFactory = StringKeyFactory,
                    options = {
                        newsFeedGroups
                            .map { nfg -> Triple<String, UiText?, DrawableResource?>(nfg.name, null, null) }
                            .sortedBy { t -> t.first }
                    }
                ),
                valid = { value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),
            Field(
                descriptor = StringFieldDescriptor(
                    key = NC.url,
                    label = Res.string.label_url,
                    toolTip = Res.string.tooltip_url
                ),
                valid = { value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),
            Field(
                descriptor = StringFieldDescriptor(
                    key = NC.imageUrl,
                    label = Res.string.label_imageUrl,
                    toolTip = Res.string.tooltip_imageUrl
                ),
                valid = { value ->
                    (value as? String)?.isNotBlank() == true
                }
            ),
            Field(
                descriptor = ListFieldDescriptor(
                    fieldClass = String::class,
                    key = NC.stopWords,
                    label = Res.string.label_stopWords,
                    toolTip = Res.string.tooltip_stopWords,
                    keyFactory = StringListKeyFactory,
                ),
                valid = { _ -> true }
            ),
        )
    }

    override fun createInstance(newFields: LinkedHashMap<NC, Field<*, *, NC>>): NewsFeedConfiguration {
        return NewsFeedConfiguration(newFields, newsFeedGroups)
    }
}
