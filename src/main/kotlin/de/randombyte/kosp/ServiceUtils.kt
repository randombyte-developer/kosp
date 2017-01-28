package de.randombyte.kosp

import org.spongepowered.api.Sponge
import kotlin.reflect.KClass

object ServiceUtils {
    fun <T : Any> getServiceOrFail(clazz: KClass<T>, failMessage: String = "'${clazz.java.simpleName}' not available!"): T {
        return Sponge.getServiceManager().provide(clazz.java).orElseThrow { RuntimeException(failMessage) }
    }
}