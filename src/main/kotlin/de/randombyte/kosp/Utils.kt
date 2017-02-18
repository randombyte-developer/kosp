package de.randombyte.kosp

import org.spongepowered.api.Sponge
import kotlin.reflect.KClass

/**
 * Miscellaneous helper functions
 */

fun <T : Any> getServiceOrFail(clazz: KClass<T>, failMessage: String = "'${clazz.java.simpleName}' not available!"): T {
    return Sponge.getServiceManager().provide(clazz.java).orElseThrow { RuntimeException(failMessage) }
}

fun executeAsConsole(command: String) = Sponge.getCommandManager().process(Sponge.getServer().console, command)