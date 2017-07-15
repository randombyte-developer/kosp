package de.randombyte.kosp

import de.randombyte.kosp.extensions.executeCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

/**
 * Miscellaneous helper functions
 */

fun <T : Any> getServiceOrFail(clazz: KClass<T>, failMessage: String = "'${clazz.java.simpleName}' is not available!"): T {
    val message = if (clazz == EconomyService::class) "No economy plugin is available!" else failMessage
    return Sponge.getServiceManager().provide(clazz.java).orElseThrow { RuntimeException(message) }
}

fun getGameProfile(playerName: String): CompletableFuture<GameProfile> = Sponge.getServer().gameProfileManager.get(playerName)

fun executeAsConsole(command: String) = Sponge.getServer().console.executeCommand(command)

/**
 * Use this instead of TextTemplate.of(...) to workaround [this issue](https://github.com/SpongePowered/SpongeCommon/issues/1152).
 */
fun fixedTextTemplateOf(vararg elements: Any): TextTemplate = TextTemplate.of(Text.EMPTY, *elements)