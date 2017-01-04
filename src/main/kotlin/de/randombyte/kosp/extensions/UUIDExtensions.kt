package de.randombyte.kosp.extensions

import de.randombyte.kosp.ServiceUtils
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.world.World
import java.util.*

fun UUID.getPlayer(): Player? = Sponge.getServer().getPlayer(this).orNull()
fun UUID.getUser(): User? = ServiceUtils
        .getServiceOrFail(UserStorageService::class, "UserStorageService not available!")
        .get(this).orNull()
fun UUID.getWorld(): World? = Sponge.getServer().getWorld(this).orNull()