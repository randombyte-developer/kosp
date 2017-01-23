package de.randombyte.kosp.config.serializer

import de.randombyte.kosp.config.serializer.SimpleTextTemplateDeserializer.StringOrMatch.Matching
import de.randombyte.kosp.config.serializer.SimpleTextTemplateDeserializer.StringOrMatch.NotMatching
import de.randombyte.kosp.config.serializer.SimpleTextTemplateTypeSerializer.COMMAND_PREFIX
import de.randombyte.kosp.config.serializer.SimpleTextTemplateTypeSerializer.SUGGEST_COMMAND_PREFIX
import de.randombyte.kosp.extensions.action
import de.randombyte.kosp.extensions.format
import de.randombyte.kosp.extensions.toArg
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.text.*
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextFormat
import java.net.MalformedURLException
import java.net.URL

object SimpleTextTemplateDeserializer {
    fun deserialize(node: ConfigurationNode): TextTemplate {
        return TextTemplate.of(*getElements(node.string).toTypedArray())
    }

    // Matches a markdown link like '[Click here](https://www.google.de)' or '[GOOD WEATHER](/weather clear)'
    private val MARKDOWN_LINK_REGEX = "\\[(.+?)\\]\\((.+?)\\)".toRegex()

    // matches arguments like '{number}'
    private val ARGUMENTS_REGEX = "\\${TextTemplate.DEFAULT_OPEN_ARG}(\\w+)\\${TextTemplate.DEFAULT_CLOSE_ARG}".toRegex()

    private fun getElements(string : String): List<TextElement> {
        val stringsAndLinks = parseLinks(string)
        val argsAndTextAndLinks = stringsAndLinks.map {
            when (it) {
                is StringOrText.MyText -> {
                    listOf(it.text)
                }
                is StringOrText.MyString -> {
                    parseFormattedTextAndArguments(it.string)
                }
            }
        }.flatten()

        val elements = transferLastFormats(argsAndTextAndLinks)

        return elements
    }

    private fun parseLinks(string : String): List<StringOrText> {
        return string.categoriseByIsMatchingRegex(MARKDOWN_LINK_REGEX).map {
            when (it) {
                is Matching -> {
                    val linkText = it.matchResult.groupValues[1]
                    val linkDestination = it.matchResult.groupValues[2]

                    val clickAction = when {
                        linkDestination.startsWith(SUGGEST_COMMAND_PREFIX) -> TextActions.suggestCommand(linkDestination.removePrefix(SUGGEST_COMMAND_PREFIX))
                        linkDestination.startsWith(COMMAND_PREFIX) -> TextActions.runCommand(linkDestination.removePrefix(COMMAND_PREFIX))
                        else -> {
                            try {
                                TextActions.openUrl(URL(linkDestination))
                            } catch (exception: MalformedURLException) {
                                throw RuntimeException("TextTemplate couldn't be parsed: ClickAction '$linkDestination' invalid!", exception)
                            }
                        }
                    }

                    return@map StringOrText.MyText(linkText.deserializeToText().simplify().action(clickAction))
                }

                // just normal text without click action
                is StringOrMatch.NotMatching -> StringOrText.MyString(it.string)
            }
        }
    }

    private fun parseFormattedTextAndArguments(string : String): List<TextRepresentable> {
        return string.categoriseByIsMatchingRegex(ARGUMENTS_REGEX).map {
            when (it) {
                is Matching -> {
                    val argumentName = it.matchResult.groupValues[1]
                    return@map argumentName.toArg()
                }
                is NotMatching -> {
                    it.string.deserializeToText()
                }
            }
        }
    }

    private fun transferLastFormats(texts: List<TextRepresentable>): List<TextRepresentable> {
        if (texts.size < 2) return texts

        val modifiedTexts = mutableListOf(texts.first())
        for (i in 1..texts.lastIndex) {
            val lastTextsFormat = modifiedTexts.last().toText().getLastFormat()
            val currentText = texts[i]
            val modifiedText = when (currentText) {
                is Text -> {
                    if (currentText.format == TextFormat.NONE) { // was a new format set?
                        currentText.format(lastTextsFormat) // if not, apply last one
                    } else currentText
                }
                is TextTemplate.Arg -> {
                    if (currentText.format == TextFormat.NONE) {
                        currentText.format(lastTextsFormat)
                    } else currentText
                }
                else -> currentText
            }

            modifiedTexts.add(modifiedText)
        }

        return modifiedTexts
    }

    /**
     * @return a list of [StringOrMatch]
     */
    private fun String.categoriseByIsMatchingRegex(regex : Regex): List<StringOrMatch> {
        val matchResults = regex.findAll(this)
        val matchRanges = matchResults.map { it.range }.toList()
        val ranges = fillInMissingRanges(0..lastIndex, matchRanges)

        val elements = ranges.map { range ->
            if (range.second) { // filled in range -> doesn't match the regex
                return@map NotMatching(substring(range.first))
            } else {
                val matchResult = matchResults.find { it.range == range.first }!!
                return@map Matching(matchResult)
            }
        }

        return elements
    }

    /**
     * @param ranges must be sorted in ascending order and they must not overlap each other
     *
     * @return list of ranges, true if filled in, false if it was in original [ranges]
     */
    private fun fillInMissingRanges(whole: IntRange, ranges: List<IntRange>): List<Pair<IntRange, Boolean>> {
        val toBeProcessedRanges = ranges.toMutableList()
        val completedRanges = mutableListOf<Pair<IntRange, Boolean>>()
        var position = whole.start

        while (position <= whole.endInclusive) {
            if (toBeProcessedRanges.isEmpty()) {
                // fill the end space and return directly
                completedRanges.add(Pair(position..whole.endInclusive, true))
                return completedRanges
            }

            val range = toBeProcessedRanges.removeAt(0) // like first() and remove(0)
            if (position < range.start) completedRanges.add(Pair(position..(range.start - 1), true)) // fill spaces
            completedRanges.add(Pair(range, false))
            position = range.endInclusive + 1
        }

        return completedRanges
    }

    /**
     * If this Text is empty(format, content, actions) and only has one child, return that child, otherwise
     * return this Text.
     */
    private fun Text.simplify(): Text = if (children.size == 1 && this is LiteralText &&
            content.isEmpty() && format == TextFormat.NONE &&
            !clickAction.isPresent && !shiftClickAction.isPresent && !hoverAction.isPresent) children.first() else this

    private fun Text.getLastFormat(): TextFormat = children.lastOrNull()?.getLastFormat() ?: format

    internal sealed class StringOrMatch {
        class Matching(val matchResult: MatchResult) : StringOrMatch()
        class NotMatching(val string : String) : StringOrMatch()
    }

    internal sealed class StringOrText {
        class MyString(val string : String) : StringOrText()
        class MyText(val text : Text) : StringOrText()
    }
}