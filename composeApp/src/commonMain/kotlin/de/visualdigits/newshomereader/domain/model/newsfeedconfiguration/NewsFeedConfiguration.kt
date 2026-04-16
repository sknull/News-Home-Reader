package de.visualdigits.newshomereader.domain.model.newsfeedconfiguration

import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.Field
import de.visualdigits.common.domain.model.configuration.ListFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.configuration.keyfactory.StringListKeyFactory
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_feedName
import de.visualdigits.compose.resources.label_imageUrl
import de.visualdigits.compose.resources.label_stopWords
import de.visualdigits.compose.resources.label_url
import de.visualdigits.compose.resources.tooltip_feedName
import de.visualdigits.compose.resources.tooltip_imageUrl
import de.visualdigits.compose.resources.tooltip_stopWords
import de.visualdigits.compose.resources.tooltip_url

class NewsFeedConfiguration(
    fields: LinkedHashMap<NC, Field<*,*,NC>> = LinkedHashMap()
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
        return NewsFeedConfiguration(newFields)
    }
}
