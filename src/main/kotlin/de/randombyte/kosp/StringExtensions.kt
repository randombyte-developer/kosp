package de.randombyte.kosp

import de.randombyte.kosp.TextExtensions.aqua
import de.randombyte.kosp.TextExtensions.black
import de.randombyte.kosp.TextExtensions.blue
import de.randombyte.kosp.TextExtensions.bold
import de.randombyte.kosp.TextExtensions.click
import de.randombyte.kosp.TextExtensions.darkAqua
import de.randombyte.kosp.TextExtensions.darkBlue
import de.randombyte.kosp.TextExtensions.darkGray
import de.randombyte.kosp.TextExtensions.darkGreen
import de.randombyte.kosp.TextExtensions.darkPurple
import de.randombyte.kosp.TextExtensions.darkRed
import de.randombyte.kosp.TextExtensions.gold
import de.randombyte.kosp.TextExtensions.gray
import de.randombyte.kosp.TextExtensions.green
import de.randombyte.kosp.TextExtensions.hover
import de.randombyte.kosp.TextExtensions.italic
import de.randombyte.kosp.TextExtensions.lightPurple
import de.randombyte.kosp.TextExtensions.obfuscated
import de.randombyte.kosp.TextExtensions.red
import de.randombyte.kosp.TextExtensions.reset
import de.randombyte.kosp.TextExtensions.shiftClick
import de.randombyte.kosp.TextExtensions.strikethrough
import de.randombyte.kosp.TextExtensions.underline
import de.randombyte.kosp.TextExtensions.white
import de.randombyte.kosp.TextExtensions.yellow
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction

/**
 * Helpful [String] extensions.
 * Taken from https://github.com/SpongePowered/Cookbook/blob/master/Plugin/HelloFromKotlin/src/main/kotlin/org/spongepowered/cookbook/TextFunctions.kt
 */
object StringExtensions {
    fun String.aqua(): Text = Text.of(this).aqua()
    fun String.black(): Text = Text.of(this).black()
    fun String.blue(): Text = Text.of(this).blue()
    fun String.darkAqua(): Text = Text.of(this).darkAqua()
    fun String.darkBlue(): Text = Text.of(this).darkBlue()
    fun String.darkGray(): Text = Text.of(this).darkGray()
    fun String.darkGreen(): Text = Text.of(this).darkGreen()
    fun String.darkPurple(): Text = Text.of(this).darkPurple()
    fun String.darkRed(): Text = Text.of(this).darkRed()
    fun String.gold(): Text = Text.of(this).gold()
    fun String.gray(): Text = Text.of(this).gray()
    fun String.green(): Text = Text.of(this).green()
    fun String.lightPurple(): Text = Text.of(this).lightPurple()
    fun String.red(): Text = Text.of(this).red()
    fun String.white(): Text = Text.of(this).white()
    fun String.yellow(): Text = Text.of(this).yellow()

    fun String.bold(): Text = Text.of(this).bold()
    fun String.italic(): Text = Text.of(this).italic()
    fun String.obfuscated(): Text = Text.of(this).obfuscated()
    fun String.reset(): Text = Text.of(this).reset()
    fun String.strikethrough(): Text = Text.of(this).strikethrough()
    fun String.underline(): Text = Text.of(this).underline()

    fun <T : ClickAction<*>> String.click(action: T): Text = Text.of(this).click(action)
    fun <T : HoverAction<*>> String.hover(action: T): Text = Text.of(this).hover(action)
    fun <T : ShiftClickAction<*>> String.shiftClick(action: T): Text = Text.of(this).shiftClick(action)
}