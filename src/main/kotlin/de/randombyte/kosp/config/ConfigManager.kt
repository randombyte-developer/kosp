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
                               formattingTextSerialization: Boolean = true,
                               simpleTextTemplateSerialization: Boolean = true,
                               additionalSerializers: TypeSerializerCollection.() -> Unit = { }) {

    private val typeToken: TypeToken<T> = clazz.typeToken
    private val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setShouldCopyDefaults(true)
            .setObjectMapperFactory(KospObjectMapperFactory)
            .setSerializers(TypeSerializers.getDefaultSerializers().apply {
                if (formattingTextSerialization) registerType(Text::class.typeToken, FormattingTextSerializer)
                if (simpleTextTemplateSerialization) registerType(TextTemplate::class.typeToken, SimpleTextTemplateSerializer)
                additionalSerializers.invoke(this)
            })

    @Suppress("UNCHECKED_CAST")
    fun get(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        get()
    }.invoke()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }
}