package de.randombyte.kosp.extensions

import de.randombyte.kosp.config.serializers.text.SimpleTextSerializer
import org.spongepowered.api.Sponge
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextAction
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.format.*
import org.spongepowered.api.text.serializer.TextSerializers

/**
 * Helpful [Text] extensions.
 * Taken from https://github.com/SpongePowered/Cookbook/blob/master/Plugin/HelloFromKotlin/src/main/kotlin/org/spongepowered/cookbook/TextFunctions.kt
 */

fun Text.format(format: TextFormat): Text = toBuilder().format(format).build()

fun Text.color(color: TextColor): Text = format(format.color(color))

fun Text.aqua(): Text = color(TextColors.AQUA)
fun Text.black(): Text = color(TextColors.BLACK)
fun Text.blue(): Text = color(TextColors.BLUE)
fun Text.darkAqua(): Text = color(TextColors.DARK_AQUA)
fun Text.darkBlue(): Text = color(TextColors.DARK_BLUE)
fun Text.darkGray(): Text = color(TextColors.DARK_GRAY)
fun Text.darkGreen(): Text = color(TextColors.DARK_GREEN)
fun Text.darkPurple(): Text = color(TextColors.DARK_PURPLE)
fun Text.darkRed(): Text = color(TextColors.DARK_RED)
fun Text.gold(): Text = color(TextColors.GOLD)
fun Text.gray(): Text = color(TextColors.GRAY)
fun Text.green(): Text = color(TextColors.GREEN)
fun Text.lightPurple(): Text = color(TextColors.LIGHT_PURPLE)
fun Text.red(): Text = color(TextColors.RED)
fun Text.white(): Text = color(TextColors.WHITE)
fun Text.yellow(): Text = color(TextColors.YELLOW)

fun Text.style(style: TextStyle): Text = format(format.style(style))

fun Text.bold(): Text = style(TextStyles.BOLD)
fun Text.italic(): Text = style(TextStyles.ITALIC)
fun Text.obfuscated(): Text = style(TextStyles.OBFUSCATED)
fun Text.reset(): Text = style(TextStyles.RESET)
fun Text.strikethrough(): Text = style(TextStyles.STRIKETHROUGH)
fun Text.underline(): Text = style(TextStyles.UNDERLINE)

fun <T : TextAction<*>> Text.action(action: T): Text = Text.of(action, this)

operator fun Text.plus(other: Text): Text = Text.of(this, other)
operator fun Text.plus(other: String): Text = this + other.toText()

fun Text.serialize(serializeTextActions: Boolean = true): String= if (serializeTextActions) {
    SimpleTextSerializer.serialize(this) // With TextActions
} else {
    TextSerializers.FORMATTING_CODE.serialize(this) // Without TextActions
}

// sending texts
fun Text.sendTo(vararg messageChannels: MessageChannel) {
    if (!isEmpty) messageChannels.forEach { it.send(this) }
}

fun List<Text>.sendTo(vararg messageChannels: MessageChannel) = forEach { it.sendTo(*messageChannels) }

fun Text.sendTo(vararg receivers: MessageReceiver) {
    if (!isEmpty) receivers.forEach { it.sendMessage(this) }
}

fun List<Text>.sendTo(vararg receivers: MessageReceiver) = forEach { it.sendTo(*receivers) }

fun Text.broadcast() = sendTo(Sponge.getServer().broadcastChannel)
fun List<Text>.broadcast() = sendTo(Sponge.getServer().broadcastChannel)