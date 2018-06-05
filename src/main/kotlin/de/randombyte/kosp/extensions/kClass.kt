package de.randombyte.kosp.extensions

import org.spongepowered.api.Sponge
import org.spongepowered.api.service.economy.EconomyService
import kotlin.reflect.KClass

fun <T : Any> KClass<T>.getServiceOrFail(failMessage: String = "'${this.java.simpleName}' is not available!"): T {
    val message = if (this == EconomyService::class) "No economy plugin is available!" else failMessage
    return Sponge.getServiceManager().provide(this.java).orElseThrow { RuntimeException(message) }
}