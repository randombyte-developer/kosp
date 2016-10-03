package de.randombyte.kosp

import de.randombyte.kosp.StringExtensions.toText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextAction
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

/**
 * Helpful [Text] extensions.
 * Taken from https://github.com/SpongePowered/Cookbook/blob/master/Plugin/HelloFromKotlin/src/main/kotlin/org/spongepowered/cookbook/TextFunctions.kt
 */
object TextExtensions {
    fun Text.aqua(): Text = Text.of(TextColors.AQUA, this)
    fun Text.black(): Text = Text.of(TextColors.BLACK, this)
    fun Text.blue(): Text = Text.of(TextColors.BLUE, this)
    fun Text.darkAqua(): Text = Text.of(TextColors.DARK_AQUA, this)
    fun Text.darkBlue(): Text = Text.of(TextColors.DARK_BLUE, this)
    fun Text.darkGray(): Text = Text.of(TextColors.DARK_GRAY, this)
    fun Text.darkGreen(): Text = Text.of(TextColors.DARK_GREEN, this)
    fun Text.darkPurple(): Text = Text.of(TextColors.DARK_PURPLE, this)
    fun Text.darkRed(): Text = Text.of(TextColors.DARK_RED, this)
    fun Text.gold(): Text = Text.of(TextColors.GOLD, this)
    fun Text.gray(): Text = Text.of(TextColors.GRAY, this)
    fun Text.green(): Text = Text.of(TextColors.GREEN, this)
    fun Text.lightPurple(): Text = Text.of(TextColors.LIGHT_PURPLE, this)
    fun Text.red(): Text = Text.of(TextColors.RED, this)
    fun Text.white(): Text = Text.of(TextColors.WHITE, this)
    fun Text.yellow(): Text = Text.of(TextColors.YELLOW, this)

    fun Text.bold(): Text = Text.of(TextStyles.BOLD, this)
    fun Text.italic(): Text = Text.of(TextStyles.ITALIC, this)
    fun Text.obfuscated(): Text = Text.of(TextStyles.OBFUSCATED, this)
    fun Text.reset(): Text = Text.of(TextStyles.RESET, this)
    fun Text.strikethrough(): Text = Text.of(TextStyles.STRIKETHROUGH, this)
    fun Text.underline(): Text = Text.of(TextStyles.UNDERLINE, this)

    fun <T : TextAction<*>> Text.action(action: T): Text = Text.of(action, this)

    operator fun Text.plus(other: Text): Text = Text.of(this, other)
    operator fun Text.plus(other: String): Text = this + other.toText()
}