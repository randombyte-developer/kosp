package de.randombyte.kosp.config.serializers.text

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.extensions.serialize
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

/**
 * Text that can be styled with formatting codes and some custom TextActions.
 *
 * TextActions can be written like so:
 *      - suggestCommand: '[CLICK](&/cmd)'
 *      - runCommand: '[CLICK](/cmd)'
 *      - openUrl: '[CLICK](https://www.google.de)'
 *
 *  Examples:
 *      'Example'
 *      '&cExample'
 *      '&cExample &1text'
 *      '[Click here to vote](https://www.vote.de)'
 *      '&c[Click here to vote](https://www.vote.de)'
 *      '[&2Click here for good weather](/weather clear)'
 *      '[A useful command](&/command <with> <parameters> <that> <the> <player> <fills> <in>)'
 *
 *  Note: Square brackets [] and parentheses () can't be used in the respective parts.
 *  Example:
 *      '[[Click here]](/command)' must be written as '[[Click here](/command)]'
 */
object SimpleTextTypeSerializer : TypeSerializer<Text> {
    internal const val COMMAND_PREFIX = "/"
    internal const val SUGGEST_COMMAND_PREFIX = "&"

    internal val RESET_CODE = Text.of(TextColors.RESET).serialize(serializeTextActions = false)

    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode) = deserialize(node.string)
    override fun serialize(type: TypeToken<*>, text: Text, node: ConfigurationNode) {
        node.value = serialize(text)
    }

    fun deserialize(string: String): Text = SimpleTextDeserializer.deserialize(string)
    fun serialize(text: Text): String = SimpleTextSerializer.serialize(text)
}

internal fun Text.hasAction() = clickAction.isPresent || hoverAction.isPresent || shiftClickAction.isPresent