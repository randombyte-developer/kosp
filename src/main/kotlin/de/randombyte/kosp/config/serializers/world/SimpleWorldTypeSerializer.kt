package de.randombyte.kosp.config.serializers.world

import com.google.common.reflect.TypeToken
import de.randombyte.kosp.extensions.toUUID
import de.randombyte.kosp.extensions.typeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.World
import java.util.*

object SimpleWorldTypeSerializer : TypeSerializer<World> {
    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode) =
            Sponge.getServer().getWorld(node.string.toUUID())
                    .orElseThrow { ObjectMappingException("World '${node.string}' is not available!") }

    override fun serialize(type: TypeToken<*>, world: World, node: ConfigurationNode) {
        node.setValue(UUID::class.typeToken, world.uniqueId)
    }
}