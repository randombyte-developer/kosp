package de.randombyte.kosp.config.serializers.text

import de.randombyte.kosp.config.serializers.*
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.COMMAND_PREFIX
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.RESET_CODE
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.SUGGEST_COMMAND_PREFIX
import de.randombyte.kosp.extensions.color
import de.randombyte.kosp.extensions.format
import de.randombyte.kosp.extensions.serialize
import de.randombyte.kosp.extensions.style
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.format.TextStyles

object SimpleTextSerializer {
    internal fun serialize(text: Text): String {
        val children = text.getOnlyChildren()

        val strings = mutableListOf<String>()
        for (child in children) {
            var mutableText = child
            if (mutableText.color == TextColors.NONE) mutableText = mutableText.color(TextColors.RESET)
            if (mutableText.style == TextStyles.NONE) mutableText = mutableText.style(TextStyles.RESET)

            val serializedTextString = mutableText.serialize(serializeTextActions = false)

            val finalText = if (child.clickAction.isPresent) {
                val clickAction = child.clickAction.get()
                when (clickAction) {
                    is ClickAction.RunCommand -> {
                        val command = clickAction.result
                        "[$serializedTextString]($COMMAND_PREFIX$command)"
                    }
                    is ClickAction.SuggestCommand -> {
                        val suggestedCommand = clickAction.result
                        "[$serializedTextString]($SUGGEST_COMMAND_PREFIX$suggestedCommand)"
                    }
                    is ClickAction.OpenUrl -> {
                        val urlString = clickAction.result.toExternalForm()
                        "[$serializedTextString]($urlString)"
                    }
                    else -> throw ObjectMappingException("A TextAction is unsupported: '$text'")
                }
            } else serializedTextString

            strings.add(finalText)
        }

        val joinedParts = strings.joinToString(separator = "")
        val removedDuplicatedCodes = removeDuplicatedCodes(joinedParts)
        val removedLeadingResetCodes = removeLeadingResetCode(removedDuplicatedCodes)

        return removedLeadingResetCodes
    }

    private fun Text.getOnlyChildren(): List<Text> {
        return if (children.isEmpty()) listOf(this) else {
            val thisTextWithoutChildren = toBuilder().removeAll().build()
            val childrenOfThisText = children.map { it.getOnlyChildren() }.flatten().map {
                if (it.format == TextFormat.NONE) it.format(thisTextWithoutChildren.format) else it
            }.filterNot { it.toPlain().isEmpty() }
            listOf(thisTextWithoutChildren) + childrenOfThisText
        }
    }

    /**
     * Removes the leading reset code: '&rWord&rtest' -> 'Word&rtest'
     */
    private fun removeLeadingResetCode(string: String) = string.removePrefix(RESET_CODE)

    /**
     * Removes the leading reset codes: '&r&r&rWord&rtest' -> 'Word&rtest'
     */
    private fun removeDuplicatedCodes(string: String): String {
        val blackenedTextActions = blackenTextActions(string) // see docs of blackenTextActions()
        val duplicates = findDuplicateCodes(blackenedTextActions)

        val textRangesWithoutDuplicates = fillInMissingRanges(0..(string.lastIndex), duplicates)
                .filter { it.second } // only get filled in ranges, here the text parts between the duplicates
                .map { it.first }

        val joinedTextWithoutDuplicates = textRangesWithoutDuplicates
                .map { range -> string.substring(range) }
                .joinToString(separator = "")

        return joinedTextWithoutDuplicates
    }

    /**
     * Returns where the code duplicates are, in order.
     * 'Test&6text&6is&cthis' -> the second '&6' range is returned
     */
    private fun findDuplicateCodes(string: String): List<IntRange> {
        val colorCodes = FORMATTING_CODES_REGEX.findAll(string).toList()

        var lastCode = ""
        val locatedDuplicates = mutableListOf<IntRange>()
        colorCodes.forEach { current ->
            val currentCode = current.groupValues[1]
            if (currentCode == lastCode) { // duplicated
                locatedDuplicates += current.range
            }
            lastCode = currentCode
        }

        return locatedDuplicates
    }

    /**
     * Example: '&3Text1 [&2Text](/weather clear) Text2' -> '&3Text1 ************************ Text2'
     * Is used to prevent [removeDuplicatedCodes] to think the color codes used in TextActions
     * are color codes in the normal text.
     * The example would like this when this method wouldn't be called:
     * '&3Text1 [&2Text](/weather clear) &3Text2'. It is not breaking things but confusing the user.
     */
    private fun blackenTextActions(string: String): String = string.categorizeByIsMatchingRegex(MARKDOWN_LINK_REGEX).map {
        when (it) {
            is StringPart.Matching -> {
                // Replace the whole TextAction with some symbol not recognized as color code
                "*".repeat(it.matchResult.range.coveredArea())
            }
            is StringPart.NotMatching -> it.string
        }
    }.joinToString(separator = "")

    /**
     * 12-13 => 2; 12-12=>1
     */
    private fun IntRange.coveredArea(): Int = endInclusive - (start - 1)
}