package de.randombyte.kosp.config.serializer

import de.randombyte.kosp.config.serializer.SimpleTextTemplateTypeSerializer.toFullArgumentName
import de.randombyte.kosp.extensions.format
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.serializer.TextSerializers

object SimpleTextTemplateDeserializer {
    fun deserialize(node: ConfigurationNode): TextTemplate {
        return TextTemplate.of(*getElements(node.string).toTypedArray())
    }

    private fun getElements(string: String): List<Any> {
        val elements = mutableListOf<TextRepresentable>()
        val arguments = getArguments(string)

        var remainingString = string // gets cut down while processing
        arguments.forEach { argument ->
            val fullArgumentName = argument.name.toFullArgumentName()
            val argumentIndex = remainingString.indexOf(fullArgumentName)

            if (argumentIndex >= 1) { // check if there is some text before the argument
                val front = remainingString.take(argumentIndex)
                val frontText = front.deserializeToText()
                elements.add(frontText)
            }

            // format argument like the last element
            val lastTextFormat = elements.lastOrNull()?.toText()?.getLastFormat()
            val formattedArgument = if (lastTextFormat != null) {
                argument.format(lastTextFormat)
            } else argument

            elements.add(formattedArgument)

            remainingString = cutFormattedText(remainingString, argumentIndex + fullArgumentName.length)
        }

        // check if there is some text after the last argument
        if (remainingString.isNotEmpty()) elements.add(remainingString.deserializeToText().flatten())

        return elements
    }

    // matches arguments like '{number}'
    private val ARGUMENTS_REGEX = "\\${TextTemplate.DEFAULT_OPEN_ARG}(\\w+)\\${TextTemplate.DEFAULT_CLOSE_ARG}".toRegex()

    /**
     * @return a List of [Arg]uments extracted from [string], which might look like '&cThe numer is {number}.'
     */
    private fun getArguments(string: String): List<TextTemplate.Arg> = ARGUMENTS_REGEX.findAll(string).map { matchResult ->
        // getting second group, enclosed by round bracket in regex pattern, so without '{}'
        val group = matchResult.groups[1]!!
        TextTemplate.arg(group.value).build()
    }.toList()

    /**
     * Cuts the [formattedText] at [index] but keeps the formatting by keeping the formatters(e.g. '&c')
     * in the text.
     */
    private fun cutFormattedText(formattedText: String, index: Int): String {
        val formattersBeforeCut = getFormatters(formattedText, 0..index)
        val remainingText = formattedText.substring(index)
        // reinsert the formatters
        return formattersBeforeCut.joinToString(separator = "") + remainingText
    }

    // matches color and formatting codes like '&c'; full list: 0123456789abcdefklmnor
    private val FORMATTING_CODES_REGEX = "&[\\da-fk-or]".toRegex()

    /**
     * @return a list of the formatters and where there are located at, which are in [range]
     */
    private fun getFormatters(formattedText: String, range: IntRange): List<String> = FORMATTING_CODES_REGEX
            .findAll(formattedText)
            .filter { range.contains(it.range) }
            .map { it.groups.first()!!.value }
            .toList()

    private fun String.deserializeToText() = TextSerializers.FORMATTING_CODE.deserialize(this)

    /**
     * @return true if the whole range [other] is in this range, false if not
     */
    private fun IntRange.contains(other: IntRange) = other.all { contains(it) }

    private fun Text.getLastFormat(): TextFormat = children.lastOrNull()?.getLastFormat() ?: format

    /**
     * If there is no content and only one child, higher it in hierarchy. Should only be used in this context.
     */
    private fun Text.flatten(): Text = if (this is LiteralText && content.isEmpty() && children.size == 1) children.first() else this
}