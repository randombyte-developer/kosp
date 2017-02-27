package de.randombyte.kosp.config.serializers.texttemplate

import de.randombyte.kosp.config.serializers.ARGUMENTS_REGEX
import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import de.randombyte.kosp.config.serializers.categorizeByIsMatchingRegex
import de.randombyte.kosp.config.serializers.text.SimpleTextDeserializer
import de.randombyte.kosp.extensions.deserialize
import de.randombyte.kosp.extensions.format
import de.randombyte.kosp.extensions.toArg
import de.randombyte.kosp.fixedTextTemplateOf
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.format.TextFormat

object SimpleTextTemplateDeserializer {
    internal fun deserialize(node: ConfigurationNode): TextTemplate =
            fixedTextTemplateOf(*parseArgumentsAndText(node.string).toTypedArray())

    /**
     * @return a list of parsed arguments and Text objects which were parsed from plain string that
     * may be formatted with formatting codes
     */
    private fun parseArgumentsAndText(string: String): List<TextRepresentable> {
        return string.categorizeByIsMatchingRegex(ARGUMENTS_REGEX).map {
            when (it) {
                is Matching -> {
                    val groupValues = it.matchResult.groupValues

                    val argumentFormatString = groupValues[1] // if not found -> ""
                    val argumentName = groupValues[2]

                    val argumentFormat = argumentFormatString.deserialize(deserializeTextActions = false)
                            .getLastFormat()

                    argumentName.toArg().format(argumentFormat)
                }
                is NotMatching -> SimpleTextDeserializer.deserialize(it.string)
            }
        }
    }
}

private fun Text.getLastFormat(): TextFormat = if (children.isEmpty()) format else children.last().getLastFormat()