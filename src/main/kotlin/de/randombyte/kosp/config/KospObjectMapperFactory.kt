package de.randombyte.kosp.config

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMapperFactory
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import java.util.concurrent.ExecutionException

/**
 * An [ObjectMapperFactory] that uses the [HyphenSeparatedObjectMapper].
 */
object KospObjectMapperFactory : ObjectMapperFactory {
    private val mapperCache = CacheBuilder.newBuilder()
            .weakKeys()
            .maximumSize(500)
            .build(object : CacheLoader<Class<*>, HyphenSeparatedObjectMapper<*>>(){
                override fun load(key: Class<*>) = HyphenSeparatedObjectMapper(key)
            })

    @Suppress("UNCHECKED_CAST")
    override fun <T> getMapper(type: Class<T>): ObjectMapper<T> {
        try {
            return mapperCache.get(type) as HyphenSeparatedObjectMapper<T>
        } catch (e: ExecutionException) {
            if (e.cause is ObjectMappingException) {
                throw e.cause as ObjectMappingException
            } else {
                throw ObjectMappingException(e)
            }
        }
    }
}