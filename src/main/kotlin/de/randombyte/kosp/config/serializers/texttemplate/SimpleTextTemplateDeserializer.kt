package de.randombyte.kosp.config.serializers.texttemplate

import de.randombyte.kosp.config.serializers.ARGUMENTS_REGEX
import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import de.randombyte.kosp.config.serializers.categorizeByIsMatchingRegex
import de.randombyte.kosp.config.serializers.getLastFormat
import de.randombyte.kosp.config.serializers.text.SimpleTextDeserializer
import de.randombyte.kosp.extensions.deserialize
import de.randombyte.kosp.extensions.format
import de.randombyte.kosp.extensions.toArg
import de.randombyte.kosp.fixedTextTemplateOf
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.format.TextFormat

object SimpleTextTemplateDeserializer {
    internal fun deserialize(string: String): TextTemplate =
            fixedTextTemplateOf(*parseArgumentsAndText(string).toTypedArray())

    /**
     * @return a list of parsed arguments and Text objects which were parsed from plain string that
     * may be formatted with formatting codes
     */
    private fun parseArgumentsAndText(string: String): List<TextRepresentable> {
        val textRepresentables = string.categorizeByIsMatchingRegex(ARGUMENTS_REGEX).map {
            when (it) {
                is Matching -> {
                    val groupValues = it.matchResult.groupValues

                    val argumentFormatString = groupValues[1] // if not found -> ""
                    val argumentName = groupValues[2]

                    val argumentFormat = argumentFormatString.deserialize(deserializeTextActions = false).getLastFormat()

                    argumentName.toArg().format(argumentFormat)
                }
                is NotMatching -> SimpleTextDeserializer.deserialize(it.string)
            }
        }

        return fixTextFormatting(textRepresentables)
    }

    /**
     * Example: 'Word1 {arg1} word2', everything is fine.
     * Example: '&2Word1 {arg1} word2', the '&2' must also be applied to 'word2'.
     *
     * So this function applies the last seen TextFormat to the Text(and only Texts). The Args are
     * skipped because they get their format from their custom format('{(&3)arg1}').
     */
    private fun fixTextFormatting(textRepresentables: List<TextRepresentable>): List<TextRepresentable> {
        var lastTextFormat: TextFormat = TextFormat.NONE
        return textRepresentables.map {
            if (it is Text) { // Only text, not Args
                val currentFormat = it.getLastFormat()
                if (currentFormat != TextFormat.NONE) {
                    lastTextFormat = currentFormat
                    it
                } else it.format(lastTextFormat) // Apply lastTextFormat if it doesn't have a format
            } else it // Don't fix Args
        }
    }
}