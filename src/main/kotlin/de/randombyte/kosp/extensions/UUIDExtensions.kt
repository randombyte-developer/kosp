package de.randombyte.kosp.extensions

import de.randombyte.kosp.ServiceUtils
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.world.World
import java.util.*

fun UUID.getPlayer(): Player? = Sponge.getServer().getPlayer(this).value()
fun UUID.getUser(): User? = ServiceUtils
        .getServiceOrFail(UserStorageService::class.java, "UserStorageService not available!")
        .get(this).value()
fun UUID.getWorld(): World? = Sponge.getServer().getWorld(this).value()