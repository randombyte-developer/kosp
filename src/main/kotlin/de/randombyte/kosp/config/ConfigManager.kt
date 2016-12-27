package de.randombyte.kosp.config

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.extensions.typeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.DefaultObjectMapperFactory
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import kotlin.reflect.KClass

/**
 * A simple configuration manager.
 */
class ConfigManager <T : Any> (private val configLoader: ConfigurationLoader<CommentedConfigurationNode>,
                               clazz: KClass<T>,
                               hyphenSeparatedKeys: Boolean = true,
                               formattingTextSerialization: Boolean = true,
                               simpleTextTemplateSerialization: Boolean = true,
                               serializers: TypeSerializerCollection = TypeSerializers.getDefaultSerializers()) {

    private val typeToken: TypeToken<T> = clazz.typeToken
    private val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setShouldCopyDefaults(true)
            .setObjectMapperFactory({
                if (hyphenSeparatedKeys) KospObjectMapperFactory else DefaultObjectMapperFactory.getInstance()
            }.invoke())
            .setSerializers(serializers.apply {
                if (formattingTextSerialization) registerType(Text::class.typeToken, FormattingTextSerializer)
                if (simpleTextTemplateSerialization) registerType(TextTemplate::class.typeToken, SimpleTextTemplateSerializer)
            })

    @Suppress("UNCHECKED_CAST")
    fun get(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        get()
    }.invoke()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }
}