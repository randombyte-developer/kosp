package de.randombyte.kosp.extensions

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.world.World
import java.util.*
import java.util.concurrent.CompletableFuture

fun String.toUUID(): UUID = UUID.fromString(this)

fun UUID.getPlayer(): Player? = Sponge.getServer().getPlayer(this).orNull()
fun UUID.getUser(): User? = UserStorageService::class.getServiceOrFail(failMessage = "UserStorageService not available!").get(this).orNull()
fun UUID.getGameProfile(): CompletableFuture<GameProfile> = Sponge.getServer().gameProfileManager.get(this)
fun UUID.getWorld(): World? = Sponge.getServer().getWorld(this).orNull()