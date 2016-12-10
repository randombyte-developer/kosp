package de.randombyte.kosp

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextAction
import java.util.UUID

/**
 * Helpful [String] extensions.
 * Taken from https://github.com/SpongePowered/Cookbook/blob/master/Plugin/HelloFromKotlin/src/main/kotlin/org/spongepowered/cookbook/TextFunctions.kt
 */
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

fun String.toUUID(): UUID = UUID.fromString(this)