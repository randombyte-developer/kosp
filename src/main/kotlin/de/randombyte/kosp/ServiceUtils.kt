package de.randombyte.kosp

import org.spongepowered.api.Sponge
import kotlin.reflect.KClass

object ServiceUtils {
    fun <T : Any> getServiceOrFail(clazz: KClass<T>, failMsg: String): T {
        return Sponge.getServiceManager().provide(clazz.java).orElseThrow { RuntimeException(failMsg) }
    }
}