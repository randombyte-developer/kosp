package de.randombyte.kosp

import org.spongepowered.api.Sponge
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import kotlin.reflect.KClass

/**
 * Miscellaneous helper functions
 */

fun <T : Any> getServiceOrFail(clazz: KClass<T>, failMessage: String = "'${clazz.java.simpleName}' is not available!"): T {
    return Sponge.getServiceManager().provide(clazz.java).orElseThrow { RuntimeException(failMessage) }
}

fun executeAsConsole(command: String) = Sponge.getCommandManager().process(Sponge.getServer().console, command)

/**
 * Use this instead of TextTemplate.of(...) to workaround [this issue](https://github.com/SpongePowered/SpongeCommon/issues/1152).
 */
fun fixedTextTemplateOf(vararg elements: Any): TextTemplate = TextTemplate.of(Text.EMPTY, *elements)