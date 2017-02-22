package de.randombyte.kosp.config.serializers.text

import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.COMMAND_PREFIX
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.FORMATTING_CODES_REGEX
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.SUGGEST_COMMAND_PREFIX
import de.randombyte.kosp.extensions.color
import de.randombyte.kosp.extensions.format
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

            val serializedTextString = mutableText.serializeToString()

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
        return strings.joinToString(separator = "")
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