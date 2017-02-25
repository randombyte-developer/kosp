package de.randombyte.kosp.config.serializers

import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import de.randombyte.kosp.extensions.format
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.format.TextFormat

internal sealed class StringPart {
    class Matching(val matchResult: MatchResult) : StringPart()
    class NotMatching(val string: String) : StringPart()
}

internal fun String.categorizeByIsMatchingRegex(regex: Regex): List<StringPart> {
    val matchResults = regex.findAll(this)
    val matchRanges = matchResults.map { it.range }.toList()
    val ranges = fillInMissingRanges(0..lastIndex, matchRanges)

    val elements = ranges.map { entry ->
        val rangeIsFilledIn = entry.second
        if (rangeIsFilledIn) NotMatching(substring(entry.first))
        else Matching(matchResults.find { it.range == entry.first }!!)
    }

    return elements
}

/**
 * @param ranges must be sorted in ascending order and they must not overlap each other
 *
 * @return list of ranges, true if filled in, false if it was in original [ranges]
 */
internal fun fillInMissingRanges(whole: IntRange, ranges: List<IntRange>): List<Pair<IntRange, Boolean>> {
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
 * Applies the format of the previous element to the current text/texttemplate to it if it doesn't have an own TextFormat.
 */
internal fun transferLastFormats(texts: List<TextRepresentable>) = texts.mapInContextToPredecessor { current, predecessor ->
    val predecessorFormat = predecessor.toText().getLastFormat()
    when (current) {
        is Text -> {
            if (current.format == TextFormat.NONE) { // was a new format set?
                current.format(predecessorFormat) // if not, apply last one
            } else current
        }
        is TextTemplate.Arg -> {
            if (current.format == TextFormat.NONE) {
                current.format(predecessorFormat)
            } else current
        }
        else -> current
    }
}

private fun <T> List<T>.mapInContextToPredecessor(transform: (current: T, predecessor: T) -> T): List<T> {
    // that only one element can't be modified in context to some other element because there is no other one
    if (size <= 1) return this

    val transformedList = mutableListOf(first())

    (1..lastIndex).forEach { i ->
        val currentElement = get(i)
        val previousElement = transformedList.last()

        transformedList.add(transform(currentElement, previousElement))
    }

    return transformedList
}

private fun Text.getLastFormat(): TextFormat = children.lastOrNull()?.getLastFormat() ?: format