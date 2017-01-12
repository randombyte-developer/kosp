package de.randombyte.kosp.config.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

object FormattingTextSerializer : TypeSerializer<Text> {
    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode): Text =
            TextSerializers.FORMATTING_CODE.deserialize(node.string)

    override fun serialize(type: TypeToken<*>, text: Text, node: ConfigurationNode) {
        node.value = TextSerializers.FORMATTING_CODE.serialize(text)
    }
}