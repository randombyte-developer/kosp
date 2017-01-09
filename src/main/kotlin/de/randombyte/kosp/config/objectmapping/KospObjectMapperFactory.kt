package de.randombyte.kosp.config.objectmapping

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import java.util.concurrent.ExecutionException

class KospObjectMapperFactory(val hyphenSerparatedKeys: Boolean) : ObjectMapperFactory {
    private val mapperCache = CacheBuilder.newBuilder()
            .weakKeys()
            .maximumSize(500)
            .build(object : CacheLoader<Class<*>, ObjectMapper<*>>() {
                override fun load(clazz: Class<*>): ObjectMapper<*> {
                    return if (hyphenSerparatedKeys) HyphenSeparatedObjectMapper(clazz) else KospObjectMapper(clazz)
                }
            })

    /**
     * The same implementation as the other two subclasses but ported to Kotlin.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> getMapper(type: Class<T>): ObjectMapper<T> {
        try {
            return mapperCache.get(type) as ObjectMapper<T>
        } catch (e: ExecutionException) {
            if (e.cause is ObjectMappingException) {
                throw e.cause as ObjectMappingException
            } else {
                throw ObjectMappingException(e)
            }
        }
    }
}