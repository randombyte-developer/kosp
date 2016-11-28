package de.randombyte.kosp

import de.randombyte.kosp.OptionalExtensions.value
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.user.UserStorageService
import java.util.*

object UUIDExtensions {
    fun UUID.getPlayer(): Player? = Sponge.getServer().getPlayer(this).value()
    fun UUID.getUser(): User? = ServiceUtils
            .getServiceOrFail(UserStorageService::class.java, "UserStorageService not available!")
            .get(this).value()
}