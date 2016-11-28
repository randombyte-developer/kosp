package de.randombyte.kosp

import org.spongepowered.api.Sponge

object ServiceUtils {
    fun <T> getServiceOrFail(clazz: Class<T>, failMsg: String): T {
        return Sponge.getServiceManager().provide(clazz).orElseThrow { RuntimeException(failMsg) }
    }
}