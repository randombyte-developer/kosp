package de.randombyte.kosp

import de.randombyte.kosp.TextExtensions.action
import de.randombyte.kosp.TextExtensions.aqua
import de.randombyte.kosp.TextExtensions.black
import de.randombyte.kosp.TextExtensions.blue
import de.randombyte.kosp.TextExtensions.bold
import de.randombyte.kosp.TextExtensions.darkAqua
import de.randombyte.kosp.TextExtensions.darkBlue
import de.randombyte.kosp.TextExtensions.darkGray
import de.randombyte.kosp.TextExtensions.darkGreen
import de.randombyte.kosp.TextExtensions.darkPurple
import de.randombyte.kosp.TextExtensions.darkRed
import de.randombyte.kosp.TextExtensions.gold
import de.randombyte.kosp.TextExtensions.gray
import de.randombyte.kosp.TextExtensions.green
import de.randombyte.kosp.TextExtensions.italic
import de.randombyte.kosp.TextExtensions.lightPurple
import de.randombyte.kosp.TextExtensions.obfuscated
import de.randombyte.kosp.TextExtensions.plus
import de.randombyte.kosp.TextExtensions.red
import de.randombyte.kosp.TextExtensions.reset
import de.randombyte.kosp.TextExtensions.strikethrough
import de.randombyte.kosp.TextExtensions.underline
import de.randombyte.kosp.TextExtensions.white
import de.randombyte.kosp.TextExtensions.yellow
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextAction
import java.util.*

/**
 * Helpful [String] extensions.
 * Taken from https://github.com/SpongePowered/Cookbook/blob/master/Plugin/HelloFromKotlin/src/main/kotlin/org/spongepowered/cookbook/TextFunctions.kt
 */
object StringExtensions {
    fun String.toText(): Text = Text.of(this)

    fun String.aqua(): Text = toText().aqua()
    fun String.black(): Text = toText().black()
    fun String.blue(): Text = toText().blue()
    fun String.darkAqua(): Text = toText().darkAqua()
    fun String.darkBlue(): Text = toText().darkBlue()
    fun String.darkGray(): Text = toText().darkGray()
    fun String.darkGreen(): Text = toText().darkGreen()
    fun String.darkPurple(): Text = toText().darkPurple()
    fun String.darkRed(): Text = toText().darkRed()
    fun String.gold(): Text = toText().gold()
    fun String.gray(): Text = toText().gray()
    fun String.green(): Text = toText().green()
    fun String.lightPurple(): Text = toText().lightPurple()
    fun String.red(): Text = toText().red()
    fun String.white(): Text = toText().white()
    fun String.yellow(): Text = toText().yellow()

    fun String.bold(): Text = toText().bold()
    fun String.italic(): Text = toText().italic()
    fun String.obfuscated(): Text = toText().obfuscated()
    fun String.reset(): Text = toText().reset()
    fun String.strikethrough(): Text = toText().strikethrough()
    fun String.underline(): Text = toText().underline()

    fun <T : TextAction<*>> String.action(action: T): Text = toText().action(action)

    operator fun String.plus(other: Text): Text = toText() + other

    fun String.toUUID(): UUID = UUID.fromString(this)
}