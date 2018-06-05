package de.randombyte.kosp.config

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.config.serializers.date.SimpleDateTypeSerializer
import de.randombyte.kosp.config.serializers.duration.SimpleDurationTypeSerializer
import de.randombyte.kosp.extensions.typeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import java.time.Duration
import java.util.*

/**
 * A class for one specific config file which handles generating configs if it isn't present and various
 * custom type serializers.
 */
class ConfigManager <T : Any> (val configLoader: ConfigurationLoader<CommentedConfigurationNode>,
                               clazz: Class<T>,
                               simpleTextSerialization: Boolean = true,
                               simpleTextTemplateSerialization: Boolean = true,
                               simpleDurationSerialization: Boolean = true,
                               simpleDateSerialization: Boolean = true,
                               additionalSerializers: TypeSerializerCollection.() -> Any = { }) {

    private val typeToken: TypeToken<T> = clazz.kotlin.typeToken
    private val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setShouldCopyDefaults(true)
            .setSerializers(TypeSerializers.getDefaultSerializers().newChild().apply {
                if (simpleDurationSerialization) registerType(Duration::class.typeToken, SimpleDurationTypeSerializer)
                if (simpleDateSerialization) registerType(Date::class.typeToken, SimpleDateTypeSerializer)
                additionalSerializers.invoke(this)
            })

    /**
     * Returns the saved config. If none exists a new one is generated and already saved.
     */
    @Suppress("UNCHECKED_CAST")
    fun load(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        load()
    }.invoke()

    @Deprecated("Use load() instead", ReplaceWith("load()"))
    fun get(): T = load()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }

    /**
     * get() already generates the config when none exists but this method also inserts missing nodes
     * and reformats the structure.
     */
    fun generate() = save(load())
}