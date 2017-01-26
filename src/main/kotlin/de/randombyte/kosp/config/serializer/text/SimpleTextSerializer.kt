package de.randombyte.kosp.config.serializer.text

import de.randombyte.kosp.config.serializer.text.SimpleTextTypeSerializer.COMMAND_PREFIX
import de.randombyte.kosp.config.serializer.text.SimpleTextTypeSerializer.FORMATTING_CODES_REGEX
import de.randombyte.kosp.config.serializer.text.SimpleTextTypeSerializer.SUGGEST_COMMAND_PREFIX
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction

object SimpleTextSerializer {
    internal fun serialize(text : Text): String {
        val children = text.getOnlyChildren()

        val strings = mutableListOf<String>()
        for (child in children) {
            // avoid duplicate formatting codes; may happen because the split up Text is serialized one at a time
            val contentWithFormatting = child.serializeToString()
            val lastSavedFormat = getFirstFormattingCode(strings, searchReversed = true)
            val firstNewFormat = getFirstFormattingCode(listOf(contentWithFormatting))
            val contentWithFixedFormatting = if (lastSavedFormat == firstNewFormat) {
                contentWithFormatting.replaceFirst("&$firstNewFormat", "") // remove
            } else contentWithFormatting

            val finalText = if (child.clickAction.isPresent) {
                val clickAction = child.clickAction.get()
                when (clickAction) {
                    is ClickAction.RunCommand -> {
                        val command = clickAction.result
                        "[$contentWithFixedFormatting]($COMMAND_PREFIX$command)"
                    }
                    is ClickAction.SuggestCommand -> {
                        val suggestedCommand = clickAction.result
                        "[$contentWithFixedFormatting]($SUGGEST_COMMAND_PREFIX$suggestedCommand)"
                    }
                    is ClickAction.OpenUrl -> {
                        val urlString = clickAction.result.toExternalForm()
                        "[$contentWithFixedFormatting]($urlString)"
                    }
                    else -> throw ObjectMappingException("A TextAction is unsupported: '$text'")
                }
            } else contentWithFixedFormatting

            strings.add(finalText)
        }
        return strings.joinToString(separator = "")
    }

    private fun Text.getOnlyChildren(): List<Text> {
        return if (children.isEmpty()) listOf(this) else {
            val thisTextWithoutChildren = toBuilder().removeAll().build()
            val childrenOfThisText = children.map { it.getOnlyChildren() }.flatten()
            listOf(thisTextWithoutChildren) + childrenOfThisText
        }
    }

    /**
     * @return e.g. '2' from 'Example &2example &4example' or '4' if [searchReversed] is true
     */
    private fun getFirstFormattingCode(strings: List<String>, searchReversed: Boolean = false): String? {
        strings.run { if (searchReversed) asReversed() else this }.forEach { string ->
            val match = FORMATTING_CODES_REGEX.findAll(string).toList().run { if (searchReversed) lastOrNull() else firstOrNull() }
            if (match != null) return match.groupValues[1]
        }

        return null
    }
}