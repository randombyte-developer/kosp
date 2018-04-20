package de.randombyte.kosp.config.serializers.texttemplate

import de.randombyte.kosp.config.serializers.getLastFormat
import de.randombyte.kosp.config.serializers.mapInContextToPredecessor
import de.randombyte.kosp.extensions.format
import de.randombyte.kosp.extensions.serialize
import de.randombyte.kosp.fixedTextTemplateOf
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.format.TextFormat

object SimpleTextTemplateSerializer {
    internal fun serialize(textTemplate: TextTemplate, nodeName: String): String {
        checkTextTemplate(textTemplate, nodeName)
        return serializeTextTemplate(textTemplate)
    }

    private fun serializeTextTemplate(textTemplate: TextTemplate): String {
        val pseudoArguments = textTemplate.arguments.map {
            val argumentName = it.key
            val argumentFormat = it.value.format

            // example: '(&2&o)' or "" when no format is present
            val argumentFormatString = if (argumentFormat == TextFormat.NONE) "" else {
                val formatString = Text.EMPTY.format(argumentFormat).serialize(serializeTextActions = false)
                "($formatString)"
            }

            val pseudoArgumentText =
                    TextTemplate.DEFAULT_OPEN_ARG + argumentFormatString + argumentName + TextTemplate.DEFAULT_CLOSE_ARG

            argumentName to pseudoArgumentText
        }.toMap()

        /* Format every Arg itself like the previous TextRepresentable. The real format of the Arg
        is already serialized in the parentheses '(&2&o)'.
        If we were not doing this, the formats would be serialized twice, by this serializer and the
        text serializer. Then the duplicated formatting codes would be removed by the text serializer
        which breaks this custom format. */
        val fixedElements = textTemplate.elements.mapInContextToPredecessor<Any, Any> { current, predecessor ->
            if (current is TextTemplate.Arg && predecessor is TextRepresentable) {
                current.format(predecessor.toText().getLastFormat())
            } else current
        }
        val fixedTextTemplate = fixedTextTemplateOf(*fixedElements.toTypedArray())

        val text = fixedTextTemplate.apply(pseudoArguments).build()
        return text.serialize(serializeTextActions = true)
    }

    /**
     * Parses an already existing comment. The format is as follows:
     * `"<comma separated list of additional args>;<real comment>"`
     * Example: `"%currencyName,currencySymbol;Message that appears when doing..."`
     *
     * @return list of additional args and the real comment
     */
    private fun parseExistingComment(comment: String): Pair<List<String>, String?> {
        val semicolonSplits = comment.split(";", limit = 2)
        if (semicolonSplits.size != 2) throw IllegalArgumentException("Comment not in the correct format for processing!")

        val additionalArgsString = semicolonSplits[0]
        val realComment = if (semicolonSplits[1].isEmpty()) null else semicolonSplits[1]

        val additionalArgs = additionalArgsString.split(",").filter(String::isNotEmpty)
        return Pair(additionalArgs, realComment)
    }

    private fun checkTextTemplate(textTemplate: TextTemplate, nodeName: String) {
        textTemplate.firstOptionalArgument()?.let { (argumentName, _) ->
            throw ObjectMappingException("TextTemplate '$nodeName': Argument '$argumentName' is optional!")
        }
        textTemplate.firstNonSingleWordArgument()?.let { (argumentName, _) ->
            throw ObjectMappingException("TextTemplate '$nodeName': Argument '$argumentName' contains a space!")
        }
    }

    private fun TextTemplate.firstOptionalArgument() = arguments.entries.firstOrNull { it.value.isOptional }
    private fun TextTemplate.firstNonSingleWordArgument() = arguments.entries.firstOrNull { !it.key.isOneWord() }
    private fun String.isOneWord() = !contains(" ")
}