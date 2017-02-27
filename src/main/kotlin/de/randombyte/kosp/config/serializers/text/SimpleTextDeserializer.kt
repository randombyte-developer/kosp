package de.randombyte.kosp.config.serializers.text

import de.randombyte.kosp.config.serializers.MARKDOWN_LINK_REGEX
import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import de.randombyte.kosp.config.serializers.categorizeByIsMatchingRegex
import de.randombyte.kosp.config.serializers.mapInContextToPredecessor
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.COMMAND_PREFIX
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.RESET_CODE
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer.SUGGEST_COMMAND_PREFIX
import de.randombyte.kosp.extensions.*
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextFormat
import java.net.MalformedURLException
import java.net.URL

object SimpleTextDeserializer {
    internal fun deserialize(string: String): Text {
        val texts = parseLinksAndText(string)
        return Text.of(*texts.toTypedArray())
    }

    /**
     * @return the parsed links to Text(with TextActions) and the normal text(without TextAction)
     */
    private fun parseLinksAndText(string: String): List<Text> = string
            .categorizeByIsMatchingRegex(MARKDOWN_LINK_REGEX).toList().mapInContextToPredecessor { current, predecessor ->
        when (current) {
            is Matching -> {
                val linkText = current.matchResult.groupValues[1]
                val linkDestination = current.matchResult.groupValues[2]

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

                deserializeToTextWithPreviousFormat(linkText, predecessor?.format).simplify().action(clickAction)
            }

            // just normal text without a click action
            is NotMatching -> deserializeToTextWithPreviousFormat(current.string, predecessor?.format)
        }
    }

    /**
     * The default deserialization method ignores reset code and doesn't set the TextFormat explicitly to NONE.
     * We fix that by splitting the string at the first occurrence of a RESET_CODE,
     * deserializing the two parts separately and then setting the formatting of the
     * second part(which is the part after the RESET_CODE) explicitly to NONE
     */
    private fun deserializeToTextWithPreviousFormat(textString: String, predecessor: TextFormat?): Text {

        fun deserializeWithPreviousFormat(textString: String, predecessor: TextFormat?): Text {
            val text = textString.deserialize(deserializeTextActions = false)
            return if (predecessor != null) text.format(predecessor) else text
        }

        fun Text.isContentEmpty() = toPlain().isEmpty()

        if (!textString.contains(RESET_CODE)) return deserializeWithPreviousFormat(textString, predecessor)

        val splits = textString.split(RESET_CODE, limit = 2)
        // there will definitely be two split elements
        val splitBeforeReset = splits[0]
        val splitAfterReset = splits[1]

        val firstText = deserializeWithPreviousFormat(splitBeforeReset, predecessor)
        val secondText = splitAfterReset.deserialize(deserializeTextActions = false).reset()

        return if (firstText.isContentEmpty()) secondText
        else if (secondText.isContentEmpty()) firstText
        // concatenating with empty text to put the other texts on the same hierarchy
        else Text.EMPTY + firstText + secondText
    }

    /**
     * If this Text is empty(format, content, actions) and only has one child, return that child, otherwise
     * return this Text.
     */
    private fun Text.simplify(): Text = if (children.size == 1 && this is LiteralText &&
            content.isEmpty() && format == TextFormat.NONE &&
            !clickAction.isPresent && !shiftClickAction.isPresent && !hoverAction.isPresent) children.first() else this
}