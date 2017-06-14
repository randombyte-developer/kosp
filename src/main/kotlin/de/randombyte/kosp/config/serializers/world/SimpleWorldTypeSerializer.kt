package de.randombyte.kosp.config.serializers.world

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.extensions.toUUID
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World

object SimpleWorldTypeSerializer : TypeSerializer<World> {
    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode) =
            Sponge.getServer().getWorld(node.string.toUUID())
                    .orElseThrow { ObjectMappingException("World '${node.string}' is not available!") }

    override fun serialize(type: TypeToken<*>, world: World, node: ConfigurationNode) {
        node.value = world.uniqueId
    }
}