package de.randombyte.kosp.config.serializers.texttemplate

import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import de.randombyte.kosp.config.serializers.categorizeByIsMatchingRegex
import de.randombyte.kosp.config.serializers.text.SimpleTextDeserializer
import de.randombyte.kosp.config.serializers.texttemplate.SimpleTextTemplateTypeSerializer.ARGUMENTS_REGEX
import de.randombyte.kosp.config.serializers.transferLastFormats
import de.randombyte.kosp.extensions.toArg
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.text.TextElement
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TextTemplate

object SimpleTextTemplateDeserializer {
    internal fun deserialize(node: ConfigurationNode): TextTemplate {
        return TextTemplate.of(*getElements(node.string).toTypedArray())
    }

    private fun getElements(string : String): List<TextElement> {
        val texts = parseArgumentsAndText(string)
        val elements = transferLastFormats(texts)
        return elements
    }

    /**
     * @return a list of parsed arguments and Text objects which were parsed from plain string that
     * may be formatted with formatting codes
     */
    private fun parseArgumentsAndText(string : String): List<TextRepresentable> {
        return string.categorizeByIsMatchingRegex(ARGUMENTS_REGEX).map {
            when (it) {
                is Matching -> it.matchResult.groupValues[1].toArg()
                is NotMatching -> SimpleTextDeserializer.deserialize(it.string)
            }
        }
    }
}