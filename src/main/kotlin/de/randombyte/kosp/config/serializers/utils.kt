package de.randombyte.kosp.config.serializers

import de.randombyte.kosp.config.serializers.StringPart.Matching
import de.randombyte.kosp.config.serializers.StringPart.NotMatching
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextFormat

internal sealed class StringPart {
    class Matching(val matchResult: MatchResult) : StringPart()
    class NotMatching(val string: String) : StringPart()
}

internal fun String.categorizeByIsMatchingRegex(regex: Regex): List<StringPart> {
    val matchResults = regex.findAll(this)
    val matchRanges = matchResults.map { it.range }.toList()
    val ranges = fillInMissingRanges(0..lastIndex, matchRanges)

    val elements = ranges.map { (key, value) ->
        val rangeIsFilledIn = value
        if (rangeIsFilledIn) NotMatching(substring(key))
        else Matching(matchResults.find { it.range == key }!!)
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

internal fun <T, R> List<T>.mapInContextToPredecessor(transform: (current: T, predecessor: R?) -> R): List<R> {
    val transformedList = mutableListOf<R>()

    (0..lastIndex).forEach { i ->
        val currentElement = get(i)
        val previousElement = transformedList.lastOrNull()

        transformedList.add(transform(currentElement, previousElement))
    }

    return transformedList
}

fun Text.getLastFormat(): TextFormat = if (children.isEmpty()) format else children.last().getLastFormat()