package de.randombyte.kosp

import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.extent.Extent

fun <E : Extent> Location<E>.createEntity(entityType: EntityType) = extent.createEntity(entityType, position)