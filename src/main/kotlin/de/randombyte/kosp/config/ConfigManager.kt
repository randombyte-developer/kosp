package de.randombyte.kosp.config

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.config.objectmapping.KospObjectMapperFactory
import de.randombyte.kosp.config.serializer.text.SimpleTextTypeSerializer
import de.randombyte.kosp.config.serializer.texttemplate.SimpleTextTemplateTypeSerializer
import de.randombyte.kosp.extensions.typeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
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
                               additionalSerializers: TypeSerializerCollection.() -> Unit = { }) {

    private val typeToken: TypeToken<T> = clazz.typeToken
    private val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setShouldCopyDefaults(true)
            .setObjectMapperFactory(KospObjectMapperFactory(hyphenSeparatedKeys))
            .setSerializers(TypeSerializers.getDefaultSerializers().newChild().apply {
                if (formattingTextSerialization) registerType(Text::class.typeToken, SimpleTextTypeSerializer)
                if (simpleTextTemplateSerialization) registerType(TextTemplate::class.typeToken, SimpleTextTemplateTypeSerializer)
                additionalSerializers.invoke(this)
            })

    @Suppress("UNCHECKED_CAST")
    fun get(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        get()
    }.invoke()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }
}