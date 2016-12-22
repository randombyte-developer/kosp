package de.randombyte.kosp.config

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.spongepowered.api.text.Text

/**
 * A simple configuration manager.
 */
class ConfigManager <T> (private val configLoader: ConfigurationLoader<CommentedConfigurationNode>,
                         clazz: Class<T>, formattingTextSerialization: Boolean = true,
                         serializers: TypeSerializerCollection = TypeSerializers.getDefaultSerializers()) {

    val typeToken: TypeToken<T> = TypeToken.of(clazz)
    val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setShouldCopyDefaults(true)
            .setObjectMapperFactory(KospObjectMapperFactory)
            .setSerializers(serializers.apply {
                if (formattingTextSerialization) registerType(TypeToken.of(Text::class.java), FormattingTextSerializer)
            })

    @Suppress("UNCHECKED_CAST")
    fun get(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        get()
    }.invoke()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }
}