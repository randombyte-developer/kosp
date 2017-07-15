package de.randombyte.kosp.config.serializers.texttemplate

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.text.TextTemplate

/**
 * Simplifies the config appearance of [TextTemplate]s. Arguments are enclosed in curly brackets.
 * Restrictions:    - Every argument is required, there are no optional ones
 *                  - Arguments must not contain spaces
 *                  - Formatting codes have to be used to apply formatting to the text
 * Example: '&cThe number is {number}.'
 *
 * To apply a format to the argument do this: '{(&2&o)number}'. Codes must be directly enclosed in
 * parentheses. More than one code can be used.
 *
 * Note: The meaning of arguments and parameters is somehow switched in [TextTemplate]s,
 * I'll keep this error in my comments to "avoid confusion".
 *
 * Comments set by `@Setting(comment = "...")` get processed when prefixed with `%`.
 * The format is described at [SimpleTextTemplateSerializer.parseExistingComment].
 *
 * TextActions can be written like so:
 *      - suggestCommand: '[CLICK](&/cmd)'
 *      - runCommand: '[CLICK](/cmd)'
 *      - openUrl: '[CLICK](https://www.google.de)'
 *
 * All these things can be combined freely.
 */
object SimpleTextTemplateTypeSerializer : TypeSerializer<TextTemplate> {
    internal const val COMMENT_NEEDS_PROCESSING_PREFIX = "%"

    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode) = deserialize(node.string)
    override fun serialize(type: TypeToken<*>, textTemplate: TextTemplate, node: ConfigurationNode) {
        node.value = serialize(textTemplate, node.key.toString())
        SimpleTextTemplateSerializer.setComment(textTemplate, node)
    }

    fun deserialize(string: String): TextTemplate = SimpleTextTemplateDeserializer.deserialize(string)
    fun serialize(textTemplate: TextTemplate, nodeName: String): String = SimpleTextTemplateSerializer.serialize(textTemplate, nodeName)
}