package de.randombyte.kosp.config

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.config.objectmapping.KospObjectMapperFactory
import de.randombyte.kosp.config.serializers.date.SimpleDateTypeSerializer
import de.randombyte.kosp.config.serializers.duration.SimpleDurationTypeSerializer
import de.randombyte.kosp.config.serializers.text.SimpleTextTypeSerializer
import de.randombyte.kosp.config.serializers.texttemplate.SimpleTextTemplateTypeSerializer
import de.randombyte.kosp.config.serializers.world.SimpleWorldTypeSerializer
import de.randombyte.kosp.extensions.typeToken
import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.world.World
import java.time.Duration
import java.util.*

/**
 * A simple configuration manager.
 */
class ConfigManager <T : Any> (val configLoader: ConfigurationLoader<CommentedConfigurationNode>,
                               clazz: Class<T>,
                               hyphenSeparatedKeys: Boolean = true,
                               simpleTextSerialization: Boolean = true,
                               simpleTextTemplateSerialization: Boolean = true,
                               simpleDurationSerialization: Boolean = true,
                               simpleDateSerialization: Boolean = true,
                               simpleWorldSerialization: Boolean = true,
                               additionalSerializers: TypeSerializerCollection.() -> Any = { }) {

    private val typeToken: TypeToken<T> = clazz.kotlin.typeToken
    private val options: ConfigurationOptions = ConfigurationOptions.defaults()
            .setShouldCopyDefaults(true)
            .setObjectMapperFactory(KospObjectMapperFactory(hyphenSeparatedKeys))
            .setSerializers(TypeSerializers.getDefaultSerializers().newChild().apply {
                if (simpleTextSerialization) registerType(Text::class.typeToken, SimpleTextTypeSerializer)
                if (simpleTextTemplateSerialization) registerType(TextTemplate::class.typeToken, SimpleTextTemplateTypeSerializer)
                if (simpleDurationSerialization) registerType(Duration::class.typeToken, SimpleDurationTypeSerializer)
                if (simpleDateSerialization) registerType(Date::class.typeToken, SimpleDateTypeSerializer)
                if (simpleWorldSerialization) registerType(World::class.typeToken, SimpleWorldTypeSerializer)
                additionalSerializers.invoke(this)
            })

    /**
     * Returns the saved config. If none exists a new one is generated and already saved.
     */
    @Suppress("UNCHECKED_CAST")
    fun get(): T = configLoader.load(options).getValue(typeToken) ?: {
        save(typeToken.rawType.newInstance() as T)
        get()
    }.invoke()

    fun save(config: T) = configLoader.apply { save(load(options).setValue(typeToken, config)) }

    /**
     * get() already generates the config when none exists but this method also inserts missing nodes
     * and reformats the structure.
     */
    fun generate() = save(get())
}