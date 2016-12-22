package de.randombyte.kosp

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.world.World
import java.util.*

val UUID.EMPTY: UUID
    get() = UUID(0, 0)

fun UUID.getPlayer(): Player? = Sponge.getServer().getPlayer(this).value()
fun UUID.getUser(): User? = ServiceUtils
        .getServiceOrFail(UserStorageService::class.java, "UserStorageService not available!")
        .get(this).value()
fun UUID.getWorld(): World? = Sponge.getServer().getWorld(this).value()