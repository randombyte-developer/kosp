package de.randombyte.kosp.extensions

import org.spongepowered.api.text.TextTemplate.Arg
import org.spongepowered.api.text.TextTemplate.Arg.Builder
import org.spongepowered.api.text.TextTemplate.arg
import org.spongepowered.api.text.format.*

fun Arg.format(format: TextFormat): Arg = toBuilder().format(format).build()

fun Arg.color(color: TextColor): Arg = format(format.color(color))

fun Arg.aqua(): Arg = color(TextColors.AQUA)
fun Arg.black(): Arg = color(TextColors.BLACK)
fun Arg.blue(): Arg = color(TextColors.BLUE)
fun Arg.darkAqua(): Arg = color(TextColors.DARK_AQUA)
fun Arg.darkBlue(): Arg = color(TextColors.DARK_BLUE)
fun Arg.darkGray(): Arg = color(TextColors.DARK_GRAY)
fun Arg.darkGreen(): Arg = color(TextColors.DARK_GREEN)
fun Arg.darkPurple(): Arg = color(TextColors.DARK_PURPLE)
fun Arg.darkRed(): Arg = color(TextColors.DARK_RED)
fun Arg.gold(): Arg = color(TextColors.GOLD)
fun Arg.gray(): Arg = color(TextColors.GRAY)
fun Arg.green(): Arg = color(TextColors.GREEN)
fun Arg.lightPurple(): Arg = color(TextColors.LIGHT_PURPLE)
fun Arg.red(): Arg = color(TextColors.RED)
fun Arg.white(): Arg = color(TextColors.WHITE)
fun Arg.yellow(): Arg = color(TextColors.YELLOW)

fun Arg.style(style: TextStyle): Arg = format(format.style(style))

fun Arg.bold(): Arg = style(TextStyles.BOLD)
fun Arg.italic(): Arg = style(TextStyles.ITALIC)
fun Arg.obfuscated(): Arg = style(TextStyles.OBFUSCATED)
fun Arg.reset(): Arg = style(TextStyles.RESET)
fun Arg.strikethrough(): Arg = style(TextStyles.STRIKETHROUGH)
fun Arg.underline(): Arg = style(TextStyles.UNDERLINE)

fun Arg.toBuilder(): Builder = arg(name)
        .format(format)
        .optional(isOptional)
        .defaultValue(defaultValue.orNull())