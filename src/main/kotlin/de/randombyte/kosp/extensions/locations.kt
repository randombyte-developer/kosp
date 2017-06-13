package de.randombyte.kosp.extensions

import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.world.Locatable
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent

fun <E : Extent> Location<E>.createEntity(entityType: EntityType) = extent.createEntity(entityType, position)

fun <T : Locatable> Iterable<T>.getNearest(location: Location<World>): T? = filter {
    it.location.extent.uniqueId == location.extent.uniqueId
}.minBy {
    it.location.position.distance(location.position)
}