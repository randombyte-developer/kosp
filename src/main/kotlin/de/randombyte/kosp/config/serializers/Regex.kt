package de.randombyte.kosp.config.serializers

import org.spongepowered.api.text.TextTemplate

/**
 * Matches color and formatting codes like '&c'; full list: 0123456789abcdefklmnor
 * But doesn't allow formatting codes to be directly enclosed in parentheses:
 * '(&2) does not work', '( &2 ) does work'. This is needed to distinguish between codes that are used
 * in arguments and the ones that are used in a normal text. Codes used in argument('{(&2)arg}') are
 * enclosed in parentheses where there must not be a space between the code and the bracket.
 */
internal val FORMATTING_CODES_REGEX = "(?<!\\()&([\\da-fk-or])(?!\\))".toRegex()

// Matches a markdown link like '[Click here](https://www.google.de)' or '[GOOD WEATHER](/weather clear)'
internal val MARKDOWN_LINK_REGEX = "\\[([^\\[\\]]+)\\]\\(([^\\(\\)]+)\\)".toRegex()

/**
 * Matches arguments like '{number}' and formatted ones '{(&2&o)number}'.
 * Groups:
 * 0: everything
 * 1: '&2&o'
 * 2: 'number'
 */
internal val ARGUMENTS_REGEX =
        "\\${TextTemplate.DEFAULT_OPEN_ARG}(?:\\((&[\\da-fk-or])+\\))?(\\w+)\\${TextTemplate.DEFAULT_CLOSE_ARG}".toRegex()