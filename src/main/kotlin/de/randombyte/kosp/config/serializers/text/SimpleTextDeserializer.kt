package de.randombyte.kosp.config.serializers.text

import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import de.randombyte.kosp.config.serializers.categorizeByIsMatchingRegex
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.COMMAND_PREFIX
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.MARKDOWN_LINK_REGEX
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.SUGGEST_COMMAND_PREFIX
import de.randombyte.kosp.config.serializers.transferLastFormats
import de.randombyte.kosp.extensions.action
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextFormat
import java.net.MalformedURLException
import java.net.URL

object SimpleTextDeserializer {
    internal fun deserialize(string: String): Text {
        val texts = parseLinksAndText(string)
        val fixedFormatsTexts = transferLastFormats(texts)
        return Text.of(*(fixedFormatsTexts.toTypedArray()))
    }

    /**
     * @return the parsed links to Text(with TextActions) and the normal text(without TextAction)
     */
    private fun parseLinksAndText(string: String): List<Text> = string.categorizeByIsMatchingRegex(MARKDOWN_LINK_REGEX).map {
        when (it) {
            is Matching -> {
                val linkText = it.matchResult.groupValues[1]
                val linkDestination = it.matchResult.groupValues[2]

                val clickAction = when {
                    linkDestination.startsWith(SUGGEST_COMMAND_PREFIX) ->
                        TextActions.suggestCommand(linkDestination.removePrefix(SUGGEST_COMMAND_PREFIX))

                    linkDestination.startsWith(COMMAND_PREFIX) ->
                        TextActions.runCommand(linkDestination.removePrefix(COMMAND_PREFIX))

                    else -> {
                        try {
                            TextActions.openUrl(URL(linkDestination))
                        } catch (exception: MalformedURLException) {
                            throw RuntimeException("TextTemplate couldn't be parsed: ClickAction '$linkDestination' invalid!", exception)
                        }
                    }
                }

                return@map linkText.deserializeToText().simplify().action(clickAction)
            }

            // just normal text without a click action
            is NotMatching -> it.string.deserializeToText()
        }
    }

    /**
     * If this Text is empty(format, content, actions) and only has one child, return that child, otherwise
     * return this Text.
     */
    private fun Text.simplify(): Text = if (children.size == 1 && this is LiteralText &&
            content.isEmpty() && format == TextFormat.NONE &&
            !clickAction.isPresent && !shiftClickAction.isPresent && !hoverAction.isPresent) children.first() else this
}